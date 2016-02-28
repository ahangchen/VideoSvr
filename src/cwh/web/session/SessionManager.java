package cwh.web.session;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.model.playback.PlayBackRes;
import cwh.web.model.realplay.M3U8Mng;
import cwh.web.model.realplay.RealPlayRes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;

/**
 * 三种锁，videoMaps，大锁，锁request
 * sessionState， 中锁， 锁session里的request
 * res，小锁，锁request里的资源
 * <p/>
 * Created by cwh on 16-1-7
 */
public class SessionManager {
    private static class Holder {
        static final SessionManager instance = new SessionManager();
    }

    public static String TAG = "SessionManager";

    public static SessionManager getInstance() {
        return Holder.instance;
    }


    // 用hashMap代替linkedList，冗余sessionId，加快查询速度
    private final HashMap<String, SessionState> sessionStates = new HashMap<String, SessionState>();

    public SessionState getSessionState(HttpServletRequest request) {
        SessionState sessionState;
        String sid = request.getParameter(CommonDefine.SID);
        synchronized (sessionStates) {
            if (sid == null) {
                // 第一次建立session或者client在后来的请求中没有带上sid
                sid = request.getSession().getId();
                // 检查是否是client忘了带上sid
                sessionState = sessionStates.get(sid);
                if (sessionState == null) {
//            sid = VMath.hashLong(request);
                    sessionState = new SessionState();
                    sessionState.setSessionId(sid);
                    // 将这个session的sid写入session对象中,用于超时清理
                    request.getSession().setAttribute(CommonDefine.SID, sid);
                    VSLog.d(TAG, "after set sid");
                    // 将sessionState写入全局的sessionStates中,而非servletContext，防止拖慢运行速度
                    sessionStates.put(sid, sessionState);
                    VSLog.d(TAG, "put session <" + sid + "," + sessionState + ">");
                } else {
                    // 确实是客户端忘了带sid，拿request的sid
                    VSLog.d(TAG, "old session" + sid + ";" + sessionState);
                }
            } else {
                sessionState = sessionStates.get(sid);
                VSLog.d(TAG, "old session" + sid + ";" + sessionState);
            }

            // 客户端给的sid不对
            if (sessionState == null) {
                VSLog.e(TAG, "session of sid " + sid + " no found, get sid from request");
                sid = request.getSession().getId();
//            sid = VMath.hashLong(request);
                // 再检查一发它是否存在
                sessionState = sessionStates.get(sid);
                if (sessionState == null) {
                    // 当做新请求
                    sessionState = new SessionState();
                    sessionState.setSessionId(sid);
                    sessionStates.put(sid, sessionState);
                } else {
                    // 居然存在，偷拿别人的sid，打回原型
                    VSLog.d(TAG, "old session " + sid + "; illegal sid offer");
                }
            }
            return sessionState;
        }
    }

    public SessionState getSessionState(String sid) {
        return sessionStates.get(sid);
    }

    public void sessionClean(SessionState sessionState) {
        sessionState.lock();
        try {
            VSLog.d(TAG, "request count:" + sessionState.getRequestStates().size());
            for (RequestState requestState : sessionState.getRequestStates()) {
                if (requestState.getRes() instanceof PlayBackRes) {
                    playBackClean((PlayBackRes) requestState.getRes(), sessionState.getSessionId());
                } else {
                    realPlayClean((RealPlayRes) requestState.getRes(), sessionState.getSessionId());
                }
            }
        } finally {
            sessionState.unLock();
        }
        synchronized (sessionStates) {
            sessionStates.remove(sessionState.getSessionId());
        }
    }

    public void playBackClean(final PlayBackRes playBackRes, String sid) {
        if (playBackRes == null) {
            // 在资源申请失败时应当删除sessionState里的这个res
            VSLog.e(TAG, "a null res in sessionState, there may be some errors when applying resource for this session: " + sid);
            return;
        }
        requestDestroy(playBackRes.getPlaybackPath(), sid, new DestroyCallback() {
            @Override
            public void onEmpty() {
                VSLog.d(TAG, "delete:" + playBackRes.getPlaybackPath());
                boolean ret = new File(CommonDefine.PLAY_BACK_DIR_PATH + "/" + playBackRes.getPlaybackPath()).delete();
                VSLog.d(TAG, "ret: " + ret);
            }
        });
    }

    public void realPlayClean(final RealPlayRes realPlayRes, String sid) {
        if (realPlayRes == null) {
            // 在资源申请失败时应当删除sessionState里的这个res
            VSLog.e(TAG, "a null res in sessionState, there may be some errors when applying resource for this session: " + sid);
            return;
        }
        requestDestroy(realPlayRes.getRealPlayPath(), sid, new DestroyCallback() {
            @Override
            public void onEmpty() {
                realPlayRes.getStopClean()[0] = true;
                VSLog.d(TAG, realPlayRes.getStopClean()[0] + "");
                realPlayRes.getConvertProcess().destroy();
                // 等ffmepg进程结束再删，否则m3u8文件会删不掉
                ThreadUtils.sleep(2000);
                boolean ret = FileUtils.rmDir(M3U8Mng.realPlayPath2Dir(realPlayRes.getRealPlayPath()));
                VSLog.d(TAG, "delete ret: " + ret);
            }
        });
    }

    // 由内存控制cache列表，保证原子性
    // <path,RequestState>
    private static final HashMap<String, RequestState> videoMaps = new HashMap<String, RequestState>();


    public RequestState isCached(String requestPath) {
        // 由外部控制同步
        if (videoMaps.containsKey(requestPath)) {
//            VSLog.d(TAG, "contain key: " + requestPath);
            return videoMaps.get(requestPath);
        } else {
            VSLog.d(TAG, "no cached " + requestPath);
            return null;
        }
    }

    public interface CacheCallback {
        void onOld(RequestState requestState);

        boolean onNew(RequestState requestState);
    }

    public interface DestroyCallback {
        void onEmpty();
    }

    public void requestVideo(String videoPath, SessionState sessionState, CacheCallback cacheCallback) {
        String sid = sessionState.getSessionId();
        VSLog.d(TAG, "session:" + sessionState.toString() + " playback size:" + sessionState.getRequestStates().size());

        VSLog.d(TAG, "synchronized begin");

        boolean isCached;
        RequestState requestState;
        synchronized (videoMaps) {
            VSLog.d(TAG, "Big lock get");
            requestState = videoMaps.get(videoPath);
            if (requestState != null) {
                if (!requestState.contain(sid)) {
//                    VSLog.d(TAG, "has cache "+videoPath +" no contain " + sid);
                    requestState.addSession(sid);
                    sessionState.lock();
                    try {
                        sessionState.addRequest(requestState);
                    } finally {
                        sessionState.unLock();
                    }
//                    VSLog.d(TAG, "add sid " + sid);
                } else { // same session request for the file again
                    VSLog.d(TAG, "Session " + sid + " cached");
                }
                isCached = true;
            } else {
//                VSLog.d(TAG, "add sid " + sid);
                isCached = false;
                requestState = new RequestState(videoPath);
                requestState.addSession(sid);
                sessionState.lock();
                try {
                    sessionState.addRequest(requestState);
                } finally {
                    sessionState.unLock();
                }
                videoMaps.put(videoPath, requestState);
            }
        }
        VSLog.d(TAG, "Big lock release, is cached: " + isCached);

        // 上面处理完请求，下面开始生成，防止不同videoPath的请求接连等待
        requestState.lock(); // 单个资源锁
        try {
            VSLog.d(TAG, "small lock get");
            // 为了避免这里拿到不正确的isCached状态，从videoMaps里删requestState要加小锁
            if (isCached) {
                // cache住，说明有请求过，但不一定已经生成了资源，
                if (requestState.isResExist()) {
                    // 资源存在就返回资源，为了避免资源使用中被删，删除资源需要加小锁
                    VSLog.d(TAG, "is cached and res is exist");
                    cacheCallback.onOld(requestState);
                } else {
                    VSLog.e(TAG, "is cached but res not exist");
                    // 前一个请求是失败的，重新发起new
                    // 应该是不会到这里的，因为前一个请求失败会从map里移除video-sessions，且移除是加大小锁的
                    reNew(cacheCallback, requestState, sessionState, videoPath);
                }
            } else { // 没有请求过，也要new
                VSLog.d(TAG, "No Cached renew");
                reNew(cacheCallback, requestState, sessionState, videoPath);
            }
        } finally {
            VSLog.d(TAG, "small lock release");
            requestState.unLock();
        }
    }

    private void reNew(CacheCallback cacheCallback, RequestState requestState, SessionState sessionState, String videoPath) {
        boolean ret = cacheCallback.onNew(requestState);
        if (ret) {
            requestState.setResExist(true);
            VSLog.d(TAG, "generate requestState success");
        } else {
            VSLog.e(TAG, "generate requestState failed, remove state");
            synchronized (videoMaps) {
                // 有点危险的锁中锁，因为有了小锁中的大锁，所以要避免大锁中的小锁，否则会死锁
                VSLog.d(TAG, "Big lock get");
                VSLog.d(TAG, "remove " + videoPath + "from videoMaps");
                videoMaps.remove(videoPath);
            }
            sessionState.lock();
            try {
                VSLog.d(TAG, "Medium lock get");
                sessionState.removeRequest(requestState);
            } finally {
                sessionState.unLock();
            }

            VSLog.d(TAG, "Big lock release");
        }
    }

    public void requestDestroy(String videoPath, String sid, DestroyCallback destroyCallback) {
        synchronized (videoMaps) {
            VSLog.d(TAG, "Big lock get");
            RequestState requestState = isCached(videoPath);
            if(requestState == null) return;
            if (requestState.isAttached()) {
                if (requestState.contain(sid)) {
                    // 因为对request里是否有session的操作都有大锁，所以不用加小锁
                    requestState.removeSession(sid);
                    VSLog.d(TAG, "remove sid :" + sid);
                    if (!requestState.isAttached()) {
                        // 这里虽然是在大锁中用小锁，但是这里用的小锁一定是已经成功生成了的文件，
                        // 而小锁中用大锁的情况只发生在，生成文件失败时，是不同的小锁
                        VSLog.d(TAG, "this video " + videoPath + " has been detached from related sessions");
                        if (requestState.isResExist()) {
                            VSLog.d(TAG, "clean " + videoPath);
                            requestState.lock();
                            VSLog.d(TAG, "small lock get");
                            try {
                                videoMaps.remove(videoPath);
                                // video文件的删除由playbackstate, realplaystate来做
                                destroyCallback.onEmpty();
                            } finally {
                                requestState.unLock();
                                VSLog.d(TAG, "small lock release");
                            }
                        } else {
                            VSLog.e(TAG, "a valid requestState, but not resource");
                            VSLog.e(TAG, "video Path:" + videoPath + "; sid:" + sid);
                        }
                        // videoClean(videoPath);
                    } else {
                        VSLog.d(TAG, "requestState still has session attached");
                    }
                } else {
                    VSLog.e(TAG, "video cached, but required session not found");
                    VSLog.e(TAG, "video Path:" + videoPath + "; sid:" + sid);
                }
            } else {
                VSLog.e(TAG, "video not found");
                VSLog.e(TAG, "video Path:" + videoPath + "; sid:" + sid);
            }
        }
        VSLog.d(TAG, "Big lock release");
    }

}

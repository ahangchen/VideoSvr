package cwh.web.session;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.model.playback.PlaybackState;
import cwh.web.model.realplay.M3U8Mng;
import cwh.web.model.realplay.RealPlayState;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;

/**
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
                    VSLog.d(TAG, "old session" + sid + ";" + sessionState);
                }
            }
            return sessionState;
        }
    }

    public SessionState getSessionState(String sid) {
        return sessionStates.get(sid);
    }

    public void sessionClean(SessionState sessionState) {
        VSLog.d(TAG, "play back count:" + sessionState.getPlaybackStates().size());
        for (PlaybackState playbackState : sessionState.getPlaybackStates()) {
            playBackClean(playbackState, sessionState.getSessionId());
        }
        VSLog.d(TAG, "real play count:" + sessionState.getRealPlayStates().size());
        for (RealPlayState realPlayState : sessionState.getRealPlayStates()) {
            realPlayClean(realPlayState, sessionState.getSessionId());
        }
        synchronized (sessionStates) {
            sessionStates.remove(sessionState.getSessionId());
        }
    }

    public void playBackClean(final PlaybackState playbackState, String sid) {
        requestDestroy(playbackState.getPlayFilePath(), sid, new DestroyCallback() {
            @Override
            public void onEmpty() {
                VSLog.d(TAG, "delete:" + playbackState.getPlayFilePath());
                boolean ret = new File(CommonDefine.PLAY_BACK_DIR_PATH + "/" + playbackState.getPlayFilePath()).delete();
                VSLog.d(TAG, "ret: " + ret);
            }
        });
    }

    public void realPlayClean(final RealPlayState realPlayState, String sid) {
        requestDestroy(realPlayState.getRealPlayPath(), sid, new DestroyCallback() {
            @Override
            public void onEmpty() {
                realPlayState.getStopClean()[0] = true;
                VSLog.d(TAG, realPlayState.getStopClean()[0] + "");
                realPlayState.getConvertProcess().destroy();
                // 等ffmepg进程结束再删，否则m3u8文件会删不掉
                ThreadUtils.sleep(2000);
                boolean ret = FileUtils.rmDir(M3U8Mng.realPlayPath2Dir(realPlayState.getRealPlayPath()));
                VSLog.d(TAG, "delete ret: " + ret);
            }
        });
    }

    // 由内存控制cache列表，保证原子性
    // <path,RequestState>
    private static final HashMap<String, RequestState> videoMaps = new HashMap<String, RequestState>();


    public RequestState isCached(String requestPath) {
        if (videoMaps.containsKey(requestPath)) {
//            VSLog.d(TAG, "contain key: " + requestPath);
            return videoMaps.get(requestPath);
        } else {
//            VSLog.d(TAG, "no cached " + requestPath);
            return null;
        }
    }

    public interface CacheCallback {
        void addTo(SessionState sessionState, RequestState requestState);

        void onOld(RequestState requestState);

        RequestState onNew();
    }

    public interface DestroyCallback {
        void onEmpty();
    }

    public void requestVideo(String videoPath, SessionState sessionState, CacheCallback cacheCallback) {
        String sid = sessionState.getSessionId();
        VSLog.d(TAG, "session:" + sessionState.toString() + " playback size:" + sessionState.getPlaybackStates().size());
        synchronized (videoMaps) {
            VSLog.d(TAG, "synchronized begin");
            RequestState requestState = videoMaps.get(videoPath);
            if (requestState != null) {
                if (!requestState.contain(sid)) {
//                    VSLog.d(TAG, "has cache "+videoPath +" no contain " + sid);
                    requestState.addSession(sid);
                    cacheCallback.addTo(sessionState, requestState);
//                    VSLog.d(TAG, "add sid " + sid);
                } else { // same sesion request for the file again
//                    VSLog.d(TAG, "contain " + sid);
                }
                cacheCallback.onOld(requestState);

            } else {
//                VSLog.d(TAG, "add sid " + sid);
                requestState = cacheCallback.onNew();
                if (requestState != null) {
                    requestState.addSession(sid);
                    cacheCallback.addTo(sessionState, requestState);
                    videoMaps.put(videoPath, requestState);
                } else {
                    VSLog.e(TAG, "generate requestState failed, do nothing");
                }
            }
        }
        VSLog.d(TAG, "synchronized end");
    }

    public void requestDestroy(String videoPath, String sid, DestroyCallback destroyCallback) {
        synchronized (videoMaps) {
            RequestState requestState = isCached(videoPath);
            if (requestState.isAttached()) {
                if (requestState.contain(sid)) {
                    requestState.removeSession(sid);
                    VSLog.d(TAG, "remove sid :" + sid);
                    if (!requestState.isAttached()) {
                        // video文件的删除由playbackstate, realplaystate来做
                        destroyCallback.onEmpty();
                        // videoClean(videoPath);
                        VSLog.d(TAG, "clean " + videoPath);
                        videoMaps.remove(videoPath);
                    }
                } else {
                    VSLog.e(TAG, "video cached, but required session not found");
                }
            } else {
                VSLog.e(TAG, "video not found");
            }
        }
    }

}

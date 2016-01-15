package cwh.web.session;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.model.playback.PlaybackState;
import cwh.web.model.realplay.M3U8Mng;
import cwh.web.model.realplay.RealPlayState;
import cwh.web.servlet.playback.PlaybackHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

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
    private HashMap<String, SessionState> sessionStates = new HashMap<String, SessionState>();

    public SessionState getSessionState(HttpServletRequest request) {
        SessionState sessionState;
        String sid = request.getParameter(CommonDefine.SID);
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
            VSLog.e(TAG, "session state no found");
            sessionState = new SessionState();
            sid = request.getSession().getId();
//            sid = VMath.hashLong(request);
            sessionState.setSessionId(sid);
            sessionStates.put(sid, sessionState);
        }
        return sessionState;
    }

    public SessionState getSessionState(String sid) {
        return sessionStates.get(sid);
    }

    public SessionState getSessionState(String sid, ServletContext context) {
        SessionState sessionState;
        if (sid == null) {
            return null;
        } else {
            sessionState = (SessionState) context.getAttribute(sid);
        }

        // 客户端给的sid不对
        if (sessionState == null) {
            return null;
        }
        return sessionState;
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
        sessionStates.remove(sessionState.getSessionId());
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
    private static final HashMap<String, RequestState> mp4Maps = new HashMap<String, RequestState>();


    public RequestState isCached(String requestPath) {
        if (mp4Maps.containsKey(requestPath)) {
//            VSLog.d(TAG, "contain key: " + requestPath);
            return mp4Maps.get(requestPath);
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

    public void requestPlayBack(String videoPath, SessionState sessionState, CacheCallback cacheCallback) {
        String sid = sessionState.getSessionId();
        VSLog.d(TAG, "session:" + sessionState.toString() + "playback size:" + sessionState.getPlaybackStates().size());
        synchronized (mp4Maps) {
            RequestState requestState = mp4Maps.get(videoPath);
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
                requestState.addSession(sid);
                cacheCallback.addTo(sessionState, requestState);
                mp4Maps.put(videoPath, requestState);
            }
        }
    }

    public void requestDestroy(String videoPath, String sid, DestroyCallback destroyCallback) {
        synchronized (mp4Maps) {
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
                        mp4Maps.remove(videoPath);
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

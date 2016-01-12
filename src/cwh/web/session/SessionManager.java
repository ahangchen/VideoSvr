package cwh.web.session;

import cwh.utils.VMath;
import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.playback.PlaybackState;
import cwh.web.model.realplay.RealPlayState;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by cwh on 16-1-7
 */
public class SessionManager {
    private static class Holder {
        static final SessionManager instance = new SessionManager();
    }

    public static SessionManager getInstance() {
        return Holder.instance;
    }

    public SessionState getSessionState(HttpServletRequest request) {
        SessionState sessionState;
        String sid = request.getParameter(CommonDefine.SID);
        if (sid == null) {
            // 第一次建立session
            sessionState = new SessionState();
            sid = VMath.hashLong(request);
            sessionState.setSessionId(sid);
            // 将这个session的sid写入session对象中
            request.getSession().setAttribute(CommonDefine.SID, sid);
            VSLog.d("after set sid");
            request.getServletContext().setAttribute(sessionState.getSessionId(), sessionState);
            VSLog.d("after set sessionState");
        } else {
            sessionState = (SessionState) request.getServletContext().getAttribute(sid);
        }

        // 客户端给的sid不对
        if (sessionState == null) {
            VSLog.e("session state no found");
            sessionState = new SessionState();
            sid = VMath.hashLong(request);
            sessionState.setSessionId(sid);
            request.getServletContext().setAttribute(sessionState.getSessionId(), sessionState);
        }
        return sessionState;
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
        VSLog.d("play back count:" + sessionState.getPlaybackStates().size());
        for (PlaybackState playbackState : sessionState.getPlaybackStates()) {
            playBackClean(playbackState);
        }
        VSLog.d("real play count:" + sessionState.getRealPlayStates().size());
        for (RealPlayState realPlayState : sessionState.getRealPlayStates()) {
            realPlayClean(realPlayState);
        }
    }

    public boolean playBackClean(PlaybackState playbackState) {
        VSLog.d(playbackState.getPlayFilePath());
        return new File(CommonDefine.PLAY_BACK_DIR_PATH + "/" + playbackState.getPlayFilePath()).delete();
    }

    public boolean realPlayClean(RealPlayState realPlayState) {
        realPlayState.getCleanToggle().setStop(true);
        VSLog.d(realPlayState.getCleanToggle().toString());
        realPlayState.getConvertProcess().destroy();
        // 等ffmepg进程结束再删，否则m3u8文件会删不掉
        ThreadUtils.sleep(2000);
        boolean ret = FileUtils.rmDir(realPlayState.getRealPlayDirPath());
        VSLog.d("delete ret: " + ret);
        return ret;
    }
}

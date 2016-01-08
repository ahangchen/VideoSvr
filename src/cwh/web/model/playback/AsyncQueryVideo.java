package cwh.web.model.playback;

import cwh.NVR.NvrService;
import cwh.NVR.play.PlayCallback;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.servlet.playback.PlaybackHelper;
import cwh.web.session.SessionManager;
import cwh.web.session.SessionState;
import cwh.web.utils.StringUtils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by cwh on 15-12-13
 */
public class AsyncQueryVideo implements Runnable {

    AsyncContext context;

    public AsyncQueryVideo(AsyncContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        VSLog.log(VSLog.DEBUG, "run");
        final HttpServletRequest request = (HttpServletRequest) context.getRequest();
        final VideoQueryParam videoQueryParam = StringUtils.DateTime2Param(
                request.getParameter(CommonDefine.CHANNEL),
                request.getParameter(CommonDefine.START),
                request.getParameter(CommonDefine.END));
        NvrService.getInstance().time2VideoPath(videoQueryParam.getChannel(),
                videoQueryParam.getStartYear(), videoQueryParam.getStartMon(), videoQueryParam.getStartDay(),
                videoQueryParam.getStartHour(), videoQueryParam.getStartMin(), videoQueryParam.getStartSec(),
                videoQueryParam.getEndYear(), videoQueryParam.getEndMon(), videoQueryParam.getEndDay(),
                videoQueryParam.getEndHour(), videoQueryParam.getEndMin(), videoQueryParam.getEndSec(), new PlayCallback() {
                    @Override
                    public void onComplete(String filePath) {
                        VSLog.d("after convert");
                        SessionState sessionState = SessionManager.getInstance().getSessionState(request);
                        VSLog.d("after get session STATE" + (sessionState == null));
                        PlaybackState playbackState = new PlaybackState(sessionState.getSessionId(), filePath);
                        VSLog.d("after new playback");
                        sessionState.addPlayback(playbackState);
                        PlaybackHelper.responseString(context.getResponse(), playbackState.toJson());
                        context.complete();
                        VSLog.log(VSLog.DEBUG, "on Complete");
                    }
                });
    }
}

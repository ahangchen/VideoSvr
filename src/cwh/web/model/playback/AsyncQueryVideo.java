package cwh.web.model.playback;

import cwh.NVR.NvrService;
import cwh.NVR.play.PlayCallback;
import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.servlet.playback.PlaybackHelper;
import cwh.web.session.SessionManager;
import cwh.web.session.SessionState;
import cwh.web.utils.StringUtils;

import javax.servlet.AsyncContext;
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
        final SessionState sessionState = SessionManager.getInstance().getSessionState(request);
        String videoPath = VideoQueryParam.formatPath(videoQueryParam);
        SessionManager.getInstance().requestPlayBack(videoPath, sessionState, new SessionManager.CacheCallback() {
            @Override
            public void addTo(SessionState sessionState, RequestState requestState) {
                sessionState.addPlayback((PlaybackState)requestState);
            }

            @Override
            public void onOld(RequestState playbackState) {
                VSLog.d("cached");
                PlaybackHelper.responseString(context.getResponse(), ((PlaybackState)playbackState).toJson(sessionState.getSessionId()));
                context.complete();
                VSLog.log(VSLog.DEBUG, "on Complete");
            }

            @Override
            public RequestState onNew() {
                // 数组实现执向引用的常引用
                final String[] playBackPath = new String[1];
                final boolean[] waitEnd = new boolean[1];
                waitEnd[0] = false;
                NvrService.getInstance().time2VideoPath(videoQueryParam.getChannel(),
                        videoQueryParam.getStartYear(), videoQueryParam.getStartMon(), videoQueryParam.getStartDay(),
                        videoQueryParam.getStartHour(), videoQueryParam.getStartMin(), videoQueryParam.getStartSec(),
                        videoQueryParam.getEndYear(), videoQueryParam.getEndMon(), videoQueryParam.getEndDay(),
                        videoQueryParam.getEndHour(), videoQueryParam.getEndMin(), videoQueryParam.getEndSec(),
                        new PlayCallback() {
                            @Override
                            public void onComplete(String filePath) {
                                playBackPath[0] = filePath;
                                waitEnd[0] = true;
                            }
                        });
                while (!waitEnd[0]) {
                    // 阻塞只为onNew返回requestState
                    ThreadUtils.sleep(1000);
                }
                VSLog.d("after convert");
                PlaybackState playbackState = new PlaybackState(sessionState.getSessionId(), playBackPath[0]);
                PlaybackHelper.responseString(context.getResponse(), playbackState.toJson(sessionState.getSessionId()));
                context.complete();
                VSLog.log(VSLog.DEBUG, "on Complete");
                return playbackState;
            }
        });

    }
}

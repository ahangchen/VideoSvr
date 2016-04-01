package cwh.web.model.playback;

import cwh.NVR.NvrService;
import cwh.NVR.play.PlayCallback;
import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.utils.process.CmdExecutor;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.servlet.ServletHelper;
import cwh.web.session.SessionManager;
import cwh.web.session.SessionState;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by cwh on 15-12-13
 */
public class AsyncQueryVideo implements Runnable {
    public static String TAG = "AsyncQueryVideo";
    AsyncContext context;

    public AsyncQueryVideo(AsyncContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        VSLog.d(TAG, "run");
        final HttpServletRequest request = (HttpServletRequest) context.getRequest();
        final PlayBackParam playBackParam = new PlayBackParam(
                request.getParameter(CommonDefine.IP),
                request.getParameter(CommonDefine.PORT),
                request.getParameter(CommonDefine.CHANNEL),
                request.getParameter(CommonDefine.START),
                request.getParameter(CommonDefine.END));
        final SessionState sessionState = SessionManager.getInstance().getSessionState(request);
        String videoPath = PlayBackParam.formatPath(playBackParam);
        SessionManager.getInstance().requestVideo(videoPath, sessionState, new SessionManager.CacheCallback() {
            @Override
            public void onOld(RequestState playbackState) {
                VSLog.d(TAG, "cached");
                VSLog.d(TAG, "touch session " + sessionState.getSessionId() + " when read video");
                SessionManager.getInstance().touch(sessionState.getSessionId());
                ServletHelper.responseString(context.getResponse(), ((PlayBackRes) playbackState.getRes()).toJson(sessionState.getSessionId()));
                context.complete();
                VSLog.d(TAG, "on Complete");
            }

            @Override
            public boolean onNew(RequestState requestState) {
                // 数组实现执向引用的常引用
                final String[] playBackPath = new String[1];
                final boolean[] waitEnd = new boolean[1];
                waitEnd[0] = false;
                NvrService.getInstance().time2VideoPath(
                        playBackParam.getIp()[0], playBackParam.getIp()[1],
                        playBackParam.getIp()[2], playBackParam.getIp()[3],
                        playBackParam.getPort(), playBackParam.getChannel(),
                        playBackParam.getStartYear(), playBackParam.getStartMon(), playBackParam.getStartDay(),
                        playBackParam.getStartHour(), playBackParam.getStartMin(), playBackParam.getStartSec(),
                        playBackParam.getEndYear(), playBackParam.getEndMon(), playBackParam.getEndDay(),
                        playBackParam.getEndHour(), playBackParam.getEndMin(), playBackParam.getEndSec(),
                        CommonDefine.PLAY_BACK_DIR_PATH + File.separator + playBackParam.toString() + CommonDefine.TMP_SUFF,
                        new PlayCallback() {
                            @Override
                            public void onComplete(String filePath) {
                                CmdExecutor.wait(String.format("ffmpeg -n -i %s -vcodec libx264 -s 640x360 %s",
                                        filePath, filePath.replace(CommonDefine.TMP_SUFF, CommonDefine.MP4)));
                                playBackPath[0] = filePath.replace(CommonDefine.TMP_SUFF, CommonDefine.MP4);
//                                        .replace(CommonDefine.PLAY_BACK_DIR_PATH + File.separator, "");
                                FileUtils.rm(filePath);
                                waitEnd[0] = true;
                            }
                        });
                int i = 50;
                while (!waitEnd[0] && i > 0) {
                    // 阻塞只为onNew返回requestState
                    ThreadUtils.sleep(1000);
                    i--;
                }
                VSLog.d(TAG, "convert time :" + i);
                if (!waitEnd[0] || !FileUtils.isExist(playBackPath[0])) {
                    if (waitEnd[0]) {
                        VSLog.e(TAG, "generate required file failed" + playBackPath[0]);
                        ServletHelper.responseString(context.getResponse(), ServletHelper.genErrCode(2, "generate required file failed"));
                    } else {
                        VSLog.e(TAG, "wait for playback over 50 seconds");
                        ServletHelper.responseString(context.getResponse(), ServletHelper.genErrCode(3, "wait for playback over 50 seconds"));
                    }
                    context.complete();
                    return false;
                }
                VSLog.d(TAG, "after convert");
                PlayBackRes playBackRes = new PlayBackRes(playBackPath[0].replace(CommonDefine.PLAY_BACK_DIR_PATH + File.separator, ""));
                VSLog.d(TAG, "touch session " + sessionState.getSessionId() + " when read video");
                SessionManager.getInstance().touch(sessionState.getSessionId());
                ServletHelper.responseString(context.getResponse(), playBackRes.toJson(sessionState.getSessionId()));
                requestState.setRes(playBackRes);
                context.complete();
                VSLog.d(TAG, "on Complete");
                return true;
            }
        });

    }
}

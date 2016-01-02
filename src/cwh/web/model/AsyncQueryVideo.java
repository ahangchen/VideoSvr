package cwh.web.model;

import cwh.NVR.NvrService;
import cwh.NVR.play.PlayCallback;
import cwh.utils.log.VSLog;
import cwh.web.servlet.PlaybackHelper;
import cwh.web.utils.StringUtils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;

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
//        new Thread(){
//            @Override
//            public void run() {
//                int i = 5;
//                while (i-->0) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (i == 1) {
//                        ServletHelper.responseString(context.getResponse(), "videocache/videoweb/1.mp4");
//                        context.complete();
//                    }
//                    VSLog.log(i);
//                }
//
//            }
//        }.start();
        ServletRequest request = context.getRequest();
        final VideoQueryParam videoQueryParam = StringUtils.DateTime2Param(request.getParameter("channel"),request.getParameter("start"), request.getParameter("end"));
        NvrService.getInstance().time2VideoPath(videoQueryParam.getChannel(),
                videoQueryParam.getStartYear(), videoQueryParam.getStartMon(), videoQueryParam.getStartDay(),
                videoQueryParam.getStartHour(), videoQueryParam.getStartMin(), videoQueryParam.getStartSec(),
                videoQueryParam.getEndYear(), videoQueryParam.getEndMon(), videoQueryParam.getEndDay(),
                videoQueryParam.getEndHour(), videoQueryParam.getEndMin(), videoQueryParam.getEndSec(), new PlayCallback() {
                    @Override
                    public void onComplete(String filePath) {
                        PlaybackHelper.responseString(context.getResponse(), filePath);
                        context.complete();
                        VSLog.log(VSLog.DEBUG, "on Complete");
                    }
                });
    }
}

package cwh.web.model;

import cwh.NVR.NVRNative;
import cwh.NVR.play.PlayCallback;
import cwh.web.servlet.ServletHelper;
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
        System.out.print("run");
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
//                    System.out.println(i);
//                }
//
//            }
//        }.start();
        ServletRequest request = context.getRequest();
        final VideoQueryParam videoQueryParam = StringUtils.DateTime2Param(request.getParameter("channel"),request.getParameter("start"), request.getParameter("end"));
        NVRNative.time2VideoPath(videoQueryParam.getChannel(),
                videoQueryParam.getStartYear(), videoQueryParam.getStartMon(), videoQueryParam.getStartDay(),
                videoQueryParam.getStartHour(), videoQueryParam.getStartMin(), videoQueryParam.getStartSec(),
                videoQueryParam.getEndYear(), videoQueryParam.getEndMon(), videoQueryParam.getEndDay(),
                videoQueryParam.getEndHour(), videoQueryParam.getEndMin(), videoQueryParam.getEndSec(), new PlayCallback() {
                    @Override
                    public void onComplete(String filePath) {
                        ServletHelper.responseString(context.getResponse(), filePath);
                        context.complete();
                        System.out.println("on Complete");
                    }
                });
    }
}

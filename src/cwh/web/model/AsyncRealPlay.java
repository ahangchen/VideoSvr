package cwh.web.model;

import cwh.utils.process.CmdExecutor;
import cwh.web.servlet.PlaybackHelper;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import java.util.Date;

/**
 * Created by cwh on 16-1-2
 */
public class AsyncRealPlay implements Runnable {
    AsyncContext context;

    public AsyncRealPlay(AsyncContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        ServletRequest request = context.getRequest();
        String ip = request.getParameter("ip");
        String port = request.getParameter("port");
        String channel = request.getParameter("channel");

        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
        String realPlayVideoPath = ip.replace(".", "-") + new Date() + PBParam.EXT;
        CmdExecutor.exec("ffmpeg -i rtsp://" + RPParam.USER + ":" + RPParam.PWD
                + "@" + ip + ":" + port
                + "/cam/realmonitor?"
                + "channel=" + channel + "&subtype=0 "
                + "-vcodec copy -f hls "
                + realPlayVideoPath + RPParam.EXT);
        try {
            // TODO 实际上应该等m3u8填满
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PlaybackHelper.responseString(context.getResponse(), realPlayVideoPath);

    }
}

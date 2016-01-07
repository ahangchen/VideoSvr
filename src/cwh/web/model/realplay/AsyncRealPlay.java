package cwh.web.model.realplay;

import cwh.utils.console.ConsoleUtils;
import cwh.utils.date.DateUtils;
import cwh.utils.log.VSLog;
import cwh.utils.process.CmdExecutor;
import cwh.web.model.CommonDefine;
import cwh.web.servlet.playback.PlaybackHelper;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import java.io.File;

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
        String realPlayVideoPath = realPlayPath(ip, channel, port);
        Process convert = AsyncRealPlay.sysRealPlay("192.168.199.108", "554", "1");//要交给全局控制，等待前端传回终止信息以终止这个进程
        PlaybackHelper.responseString(context.getResponse(), realPlayVideoPath);

    }

    public static String realPlayDir(String ip, String port, String channel) {
        String curDirPath = ip.replace(".", "-") + "-" + port + "-" + channel + "-"
                + DateUtils.formatCurDate() /*+"-"+ DateUtils.formatCurTime().replace(":","-") */;
        File curDir = new File(CommonDefine.videoFilePath + "/" + curDirPath);
        curDir.mkdir();
        return CommonDefine.videoFilePath + "/" + curDirPath ;
    }

    // 先拿到path
    public static String realPlayPath(String ip, String port, String channel) {
        return realPlayDir(ip, port, channel) + "/" + CommonDefine.rpFile + CommonDefine.M3U8;
    }

    public static String waitRealPlay(String ip, String port, String channel) {
        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
        String path = AsyncRealPlay.realPlayPath(ip,port,channel);
        VSLog.log(VSLog.DEBUG, path);
        Process convert = AsyncRealPlay.sysRealPlay(ip,port,channel);
        ConsoleUtils.waitE(convert);
        return path;
    }

    // 拿到创建出来的线程
    public static Process sysRealPlay(String ip, String port, String channel) {
        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
        String realPlayVideoPath = realPlayPath(ip, port, channel);
        return CmdExecutor.run(String.format(CommonDefine.FFMPEG_CONVERT, ip, port, channel, realPlayVideoPath));
    }


}

package cwh.web.model.realplay;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.console.ConsoleUtils;
import cwh.utils.log.VSLog;
import cwh.utils.process.CmdExecutor;
import cwh.web.model.CommonDefine;
import cwh.web.servlet.playback.PlaybackHelper;
import cwh.web.session.SessionManager;
import cwh.web.session.SessionState;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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
        final String realPlayVideoPath = M3U8Mng.realPlayPath(ip, channel, port);
        // 要交给全局控制，等待前端传回终止信息或超时以终止这个进程
        // 在这里开始转换
        final Process convert = sysRealPlay(ip, channel, port);

        SessionState sessionState = SessionManager.getInstance().getSessionState((HttpServletRequest) request);
        RealPlayState realPlayState = new RealPlayState(sessionState.getSessionId(), M3U8Mng.realPlayPath2Dir(realPlayVideoPath), convert);
        sessionState.addRealPlay(realPlayState);

        ThreadUtils.sleep(1000);
        PlaybackHelper.responseString(context.getResponse(), realPlayState.toJson());
        // 后台清理
        ThreadUtils.runInBackGround(new Runnable() {
            @Override
            public void run() {
                M3U8Mng.timelyClean(M3U8Mng.realPlayPath2Dir(realPlayVideoPath));
            }
        });

        // 后台等待
        try {
            convert.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
        public static String waitRealPlay(String ip, String port, String channel) {
        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
        String path = M3U8Mng.realPlayPath(ip, port, channel);
        VSLog.log(VSLog.DEBUG, path);
        Process convert = sysRealPlay(ip, port, channel);
        ConsoleUtils.waitE(convert);
        return path;
    }

    // 拿到创建出来的线程
    public static Process sysRealPlay(String ip, String port, String channel) {
        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
        String realPlayVideoPath = M3U8Mng.realPlayPath(ip, port, channel);
        return CmdExecutor.run(String.format(CommonDefine.FFMPEG_CONVERT, ip, port, channel, realPlayVideoPath));
    }


}

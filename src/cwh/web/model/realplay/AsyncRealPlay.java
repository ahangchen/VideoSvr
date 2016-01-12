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
        final String ip = request.getParameter(CommonDefine.IP);
        final String port = request.getParameter(CommonDefine.PORT);
        final String channel = request.getParameter(CommonDefine.CHANNEL);

        final SessionState sessionState = SessionManager.getInstance().getSessionState((HttpServletRequest)request);
        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
        final String realPlayVideoPath = M3U8Mng.realPlayPath(ip, port, channel);
        SessionManager.getInstance().requestVideo(realPlayVideoPath, sessionState.getSessionId(), new SessionManager.CacheCallback() {
            @Override
            public void onOld(String cachePath) {
                final CleanToggle cleanToggle = new CleanToggle();
                // TODO 先写一个假进程，之后再想这块怎么处理
                Process convert = CmdExecutor.run("ls");
                RealPlayState realPlayState = new RealPlayState(sessionState.getSessionId(), M3U8Mng.realPlayPath2Dir(realPlayVideoPath), convert, cleanToggle);
                sessionState.addRealPlay(realPlayState);

                PlaybackHelper.responseString(context.getResponse(), realPlayState.toJson());
            }

            @Override
            public void onNew() {
                // 在这里发起转换，然后把进程交给Session，等待前端传回终止信息或超时以终止这个进程
                final Process convert = sysRealPlay(ip, port, channel, realPlayVideoPath);

                final CleanToggle cleanToggle = new CleanToggle();
                RealPlayState realPlayState = new RealPlayState(sessionState.getSessionId(), M3U8Mng.realPlayPath2Dir(realPlayVideoPath), convert, cleanToggle);
                sessionState.addRealPlay(realPlayState);

                ThreadUtils.sleep(5000);
                PlaybackHelper.responseString(context.getResponse(), realPlayState.toJson());
                // 后台清理
                ThreadUtils.runInBackGround(new Runnable() {
                    @Override
                    public void run() {
                        M3U8Mng.timelyClean(M3U8Mng.realPlayPath2Dir(realPlayVideoPath), cleanToggle);
                    }
                });
            }
        });

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

    // 拿到创建出来的线程
    public static Process sysRealPlay(String ip, String port, String channel, String realPlayVideoPath) {
        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
        VSLog.d("ffmpeg to :"+realPlayVideoPath);
        return CmdExecutor.run(String.format(CommonDefine.FFMPEG_CONVERT, ip, port, channel, realPlayVideoPath));
    }


}

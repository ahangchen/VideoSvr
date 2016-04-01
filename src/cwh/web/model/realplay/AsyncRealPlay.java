package cwh.web.model.realplay;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.console.ConsoleUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.utils.process.CmdExecutor;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.servlet.ServletHelper;
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
    public static String TAG = "AsyncRealPlay";
    public AsyncRealPlay(AsyncContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        final ServletRequest request = context.getRequest();
        final String ip = request.getParameter(CommonDefine.IP);
        final String port = request.getParameter(CommonDefine.PORT);
        final String channel = request.getParameter(CommonDefine.CHANNEL);
        final String nvrIP = request.getParameter(CommonDefine.NVR_IP);
        final String nvrPort = request.getParameter(CommonDefine.NVR_PORT);

        final SessionState sessionState = SessionManager.getInstance().getSessionState((HttpServletRequest) request);
        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor\?channel=1\&subtype=0 -vcodec copy -f hls out.m3u8
        final String realPlayVideoPath = M3U8Mng.realPlayPath(ip, port, channel);
        final boolean[] stopClean = new boolean[1];// 控制是否停止清理
        stopClean[0] = false;
        // 整个调用与回调都是同步阻塞进行的，需要等它们都执行完才会起clean。之所以用回调写是为了隐藏session管理细节，并且与playback兼容
        SessionManager.getInstance().requestVideo(realPlayVideoPath, sessionState, new SessionManager.CacheCallback() {
            @Override
            public void onOld(RequestState requestState) {
                VSLog.d(TAG, "cached");
                RealPlayRes realPlayRes = (RealPlayRes) requestState.getRes();
                VSLog.d(TAG, "touch session " + sessionState.getSessionId() + " when read video");
                SessionManager.getInstance().touch(sessionState.getSessionId());
                ServletHelper.responseString(context.getResponse(), realPlayRes.toJson(sessionState.getSessionId()));
                context.complete();
            }

            @Override
            public boolean onNew(RequestState requestState) {
                // 在这里发起转换，然后把进程交给Session，等待前端传回终止信息或超时以终止这个进程
                // 长期持有这两个final不知道会不会有东西泄露
                Process convert = sysRealPlay(ip, port, channel, realPlayVideoPath);
                RealPlayRes realPlayRes = new RealPlayRes(nvrIP, nvrPort, realPlayVideoPath, convert, stopClean);
                // 等m3u8生成
                boolean m3u8Ret = M3U8Mng.waitForM3U8(realPlayVideoPath);
                if (m3u8Ret && FileUtils.isExist(realPlayVideoPath)) {
                    VSLog.d(TAG, "touch session " + sessionState.getSessionId() + " when read video");
                    SessionManager.getInstance().touch(sessionState.getSessionId());
                    ServletHelper.responseString(context.getResponse(), realPlayRes.toJson(sessionState.getSessionId()));
                    context.complete();
                    requestState.setRes(realPlayRes);
                    return true;
                } else {
                    ServletHelper.responseString(context.getResponse(), ServletHelper.genErrCode(2, "generate m3u8 time out"));
                    convert.destroy();
                    FileUtils.rmDir(M3U8Mng.realPlayDir2Path(realPlayVideoPath));
                    context.complete();
                    return false;
                }
            }
        });
        // 清理,可以用不开线程的方式来做，前面已经response了
//        M3U8Mng.timelyClean(M3U8Mng.realPlayPath2Dir(realPlayVideoPath), stopClean);
    }

    public static String waitRealPlay(String ip, String port, String channel) {
        // ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
        String path = M3U8Mng.realPlayPath(ip, port, channel);
        VSLog.d(TAG, path);
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
        VSLog.d(TAG, "ffmpeg to :" + realPlayVideoPath);
        return CmdExecutor.run(String.format(CommonDefine.FFMPEG_CONVERT, ip, port, channel, realPlayVideoPath));
    }

    public static void main(String[]args) {
        Process proc = sysRealPlay("222.201.137.81", "35557", "1");
//        proc.destroy();
        int i = 20;
        while (i-- > 0) {
            ThreadUtils.sleep(500);
            try {
                VSLog.d(TAG, proc.exitValue() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

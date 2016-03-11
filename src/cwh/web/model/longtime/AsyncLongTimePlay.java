package cwh.web.model.longtime;


import cwh.NVR.NvrService;
import cwh.NVR.play.PlayCallback;
import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.utils.process.CmdExecutor;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.model.playback.PlayBackParam;
import cwh.web.model.playback.PlayBackRes;
import cwh.web.servlet.longtime.LongTimeHelper;
import cwh.web.servlet.playback.PlaybackHelper;
import cwh.web.session.SessionManager;
import cwh.web.session.SessionState;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by cwh on 16-3-9
 */
public class AsyncLongTimePlay implements Runnable {
    public static String TAG = "LongTimePlay";
    AsyncContext context;

    public AsyncLongTimePlay(AsyncContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        VSLog.d(TAG, "long time run");
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
                PlaybackHelper.responseString(context.getResponse(), ((PlayBackRes) playbackState.getRes()).toJson(sessionState.getSessionId()));
                context.complete();
                VSLog.d(TAG, "on Complete");
            }

            @Override
            public boolean onNew(RequestState requestState) {
                final String tsDir = CommonDefine.PLAY_BACK_DIR_PATH + File.separator + playBackParam.toString() + CommonDefine.LONG_TIME;
                FileUtils.mkdir(tsDir);
                final String longTimeM3U8Path = tsDir + File.separator + CommonDefine.LONG_TIME_M3U8;
                FileUtils.createFile(longTimeM3U8Path);
                final int[][][] timeIntervals = LongTimeHelper.splitTime(
                        playBackParam.getStartYear(), playBackParam.getStartMon(), playBackParam.getStartDay(),
                        playBackParam.getStartHour(), playBackParam.getStartMin(), playBackParam.getStartSec(),
                        playBackParam.getEndYear(), playBackParam.getEndMon(), playBackParam.getEndDay(),
                        playBackParam.getEndHour(), playBackParam.getEndMin(), playBackParam.getEndSec());
                // 数组实现执向引用的常引用
                final String[] playBackPath = new String[1];
                final boolean[] waitEnd = new boolean[1];
                waitEnd[0] = false;
                final int[] tsIndex = new int[1];
                NvrService.getInstance().time2VideoPath(
                        playBackParam.getIp()[0], playBackParam.getIp()[1],
                        playBackParam.getIp()[2], playBackParam.getIp()[3],
                        playBackParam.getPort(), playBackParam.getChannel(),
                        timeIntervals[0][0][0], timeIntervals[0][0][1], timeIntervals[0][0][2],
                        timeIntervals[0][0][3], timeIntervals[0][0][4], timeIntervals[0][0][5],
                        timeIntervals[0][1][0], timeIntervals[0][1][1], timeIntervals[0][1][2],
                        timeIntervals[0][1][3], timeIntervals[0][1][4], timeIntervals[0][1][5],
                        tsDir + File.separator + "play0.dav",
                        new PlayCallback() {
                            @Override
                            public void onComplete(String filePath) {
                                String firstM3U8 = sysSingleMp42TS(filePath, 0, tsDir);
                                tsIndex[0] = firstM3U8(firstM3U8, longTimeM3U8Path, tsDir);
                                playBackPath[0] = longTimeM3U8Path;
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
                        PlaybackHelper.responseString(context.getResponse(), "generate required file failed");
                    } else {
                        VSLog.e(TAG, "wait for longtime over 50 seconds");
                        PlaybackHelper.responseString(context.getResponse(), "wait for longtime over 50 seconds");
                    }
                    context.complete();
                    return false;
                }
                VSLog.d(TAG, "after convert");
                LongTimeRes playBackRes = new LongTimeRes(playBackPath[0]);
                PlaybackHelper.responseString(context.getResponse(), playBackRes.toJson(sessionState.getSessionId()));
                requestState.setRes(playBackRes);
                context.complete();
                VSLog.d(TAG, "on Complete");
                ThreadUtils.runInBackGround(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i < timeIntervals.length; i++) {
                            addM3U8(i, tsIndex, playBackParam, timeIntervals, tsDir, longTimeM3U8Path);
                        }
                    }
                });
                return true;
            }
        });

    }

    public static void addM3U8(int index, final int[]tsIndex, PlayBackParam playBackParam, int[][][] timeIntervals, final String tsDir, final String longTimeM3U8Path) {
        final int[] curIndex = new int[1];
        curIndex[0] = index;
        NvrService.getInstance().time2VideoPath(
                playBackParam.getIp()[0], playBackParam.getIp()[1],
                playBackParam.getIp()[2], playBackParam.getIp()[3],
                playBackParam.getPort(), playBackParam.getChannel(),
                timeIntervals[index][0][0], timeIntervals[index][0][1], timeIntervals[index][0][2],
                timeIntervals[index][0][3], timeIntervals[index][0][4], timeIntervals[index][0][5],
                timeIntervals[index][1][0], timeIntervals[index][1][1], timeIntervals[index][1][2],
                timeIntervals[index][1][3], timeIntervals[index][1][4], timeIntervals[index][1][5],
                tsDir + File.separator + "play" + index + ".dav",
                new PlayCallback() {
                    @Override
                    public void onComplete(String filePath) {
                        String curM3U8 = sysSingleMp42TS(filePath, curIndex[0], tsDir);
                        appendM3U8(curM3U8, longTimeM3U8Path, tsIndex, tsDir);
                        FileUtils.rm(filePath);
                    }
                });
    }

    public static String sysSingleMp42TS(String tmpFilePath, int index, String tsDir) {
        CmdExecutor.wait("ffmpeg -y -i " + tmpFilePath + " -f ssegment -segment_format mpegts -segment_list "
                + tsDir + "/play" + index + ".m3u8 -vcodec libx264 " + tsDir + "/out" + index + "%03d.ts\n");
//        -segment_time 2
        return tsDir + "/play" + index + ".m3u8";
    }

    public static StringBuilder appendM3U8Line(StringBuilder buf, String lineContent, int[]curIndex, String tsDir) {
        if (!lineContent.equals("#EXT-X-ENDLIST")) {
            if (lineContent.startsWith("#")) {
                buf.append(lineContent);
                buf.append("\n");
            } else if (lineContent.startsWith("out")) {
                buf.append("out");
                buf.append(curIndex[0]);
                buf.append(".ts");
                buf.append("\n");
                FileUtils.rename(tsDir + File.separator + lineContent, tsDir + File.separator + "out" + curIndex[0] + ".ts");
                curIndex[0]++;
            }
        }
        return buf;
    }

    public static int firstM3U8(String partPath, String wholePath, final String tsDir) {
        final int[] tsIndex = new int[1];
        tsIndex[0] = 0;
        final StringBuilder buf = new StringBuilder();
        FileUtils.readLine(partPath, new FileUtils.ReadLine() {
            @Override
            public void onLine(String lineContent, int lineIndex) {
                if (lineIndex < 5) {
                    if (lineContent.startsWith("#EXT-X-TARGETDURATION:")) {
                        // fix for ts longer than 10 seconds
                        buf.append("#EXT-X-TARGETDURATION:");
                        buf.append(CommonDefine.LONG_SPLIT_INTERVAL/1000 + 2);
                        buf.append("\n");
                    } else {
                        buf.append(lineContent);
                        buf.append("\n");
                    }
                } else {
                    appendM3U8Line(buf, lineContent, tsIndex, tsDir);
                }
            }
        });
        FileUtils.append2File(wholePath, buf.toString());
        FileUtils.rm(partPath);
        return tsIndex[0];
    }

    public static int appendM3U8(final String partPath, String wholePath, final int[]curIndex, final String tsDir) {
        final StringBuilder buf = new StringBuilder();
        FileUtils.readLine(partPath, new FileUtils.ReadLine() {
            @Override
            public void onLine(String lineContent, int lineIndex) {
                if (lineIndex >= 5) {
                    appendM3U8Line(buf, lineContent, curIndex, tsDir);
                }
            }
        });
        FileUtils.append2File(wholePath, buf.toString());
        FileUtils.rm(partPath);
        return curIndex[0];
    }

    public static void main(String[] args) {
//        sysSingleMp42TS("/home/cwh/Mission/lab/data/videoweb/a-tmp.dav", 0);
    }
}

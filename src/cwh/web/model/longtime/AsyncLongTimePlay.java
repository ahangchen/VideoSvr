package cwh.web.model.longtime;


import cwh.NVR.NvrService;
import cwh.NVR.play.PlayCallback;
import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.utils.process.CmdExecutor;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.servlet.longtime.LongTimeHelper;
import cwh.web.servlet.playback.PlaybackHelper;
import cwh.web.session.SessionManager;
import cwh.web.session.SessionState;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

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
        final LongTimeParam playBackParam = new LongTimeParam(
                request.getParameter(CommonDefine.IP),
                request.getParameter(CommonDefine.PORT),
                request.getParameter(CommonDefine.CHANNEL),
                request.getParameter(CommonDefine.START));
        final SessionState sessionState = SessionManager.getInstance().getSessionState(request);
        final String tsDir = LongTimeParam.formatPath(playBackParam);
        SessionManager.getInstance().requestVideo(tsDir.replace(CommonDefine.DATA_PATH + File.separator, "") + File.separator + CommonDefine.LONG_TIME_M3U8,
                sessionState, new SessionManager.CacheCallback() {
                    @Override
                    public void onOld(RequestState longTimeState) {
                        VSLog.d(TAG, "cached");
                        PlaybackHelper.responseString(context.getResponse(), ((LongTimeRes) longTimeState.getRes()).toJson(sessionState.getSessionId()));
                        context.complete();
                        VSLog.d(TAG, "on Complete");
                    }

                    @Override
                    public boolean onNew(RequestState requestState) {
                        FileUtils.mkdir(tsDir);
                        final String longTimeM3U8Path = tsDir + File.separator + CommonDefine.LONG_TIME_M3U8;
                        FileUtils.createFile(longTimeM3U8Path);
                        // 数组实现执向引用的常引用
                        final String[] playBackPath = new String[1];
                        final boolean[] waitEnd = new boolean[1];
                        waitEnd[0] = false;
                        final int[] tsIndex = new int[1];
                        final int[] nextStart = LongTimeHelper.endTime(
                                playBackParam.getStartYear(), playBackParam.getStartMon(), playBackParam.getStartDay(),
                                playBackParam.getStartHour(), playBackParam.getStartMin(), playBackParam.getStartSec());
                        NvrService.getInstance().time2VideoPath(
                                playBackParam.getIp()[0], playBackParam.getIp()[1],
                                playBackParam.getIp()[2], playBackParam.getIp()[3],
                                playBackParam.getPort(), playBackParam.getChannel(),
                                playBackParam.getStartYear(), playBackParam.getStartMon(), playBackParam.getStartDay(),
                                playBackParam.getStartHour(), playBackParam.getStartMin(), playBackParam.getStartSec(),
                                nextStart[0], nextStart[1], nextStart[2], nextStart[3], nextStart[4], nextStart[5],
                                tsDir + File.separator + "play0.dav",
                                new PlayCallback() {
                                    @Override
                                    public void onComplete(String filePath) {
                                        String firstM3U8 = sysSingleMp42TS(filePath, 0, tsDir);
                                        if (FileUtils.isExist(firstM3U8)) {
                                            tsIndex[0] = firstM3U8(firstM3U8, longTimeM3U8Path, tsDir);
                                            playBackPath[0] = longTimeM3U8Path;
                                            FileUtils.rm(filePath);
                                        }
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
                        if (!waitEnd[0] || !FileUtils.isExist(playBackPath[0]) || FileUtils.lineCount(playBackPath[0]) <= 5) {
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
                        final LongTimeRes longTimeRes = new LongTimeRes(playBackPath[0].replace(CommonDefine.DATA_PATH + File.separator, ""));
                        PlaybackHelper.responseString(context.getResponse(), longTimeRes.toJson(sessionState.getSessionId()));
                        requestState.setRes(longTimeRes);
                        context.complete();
                        VSLog.d(TAG, "on Complete");
                        ThreadUtils.runInBackGround(new Runnable() {
                            @Override
                            public void run() {
                                int i = 1;
                                while (!longTimeRes.isStop()) {
                                    addM3U8(i, tsIndex, playBackParam, nextStart, tsDir, longTimeM3U8Path);
                                    i++;
                                }
                            }
                        });
                        return true;
                    }
                });

    }

    public static void addM3U8(int index, final int[] tsIndex, final LongTimeParam playBackParam, int[] curStart, final String tsDir, final String longTimeM3U8Path) {
        final int[] curIndex = new int[1];
        curIndex[0] = index;
        int[] curEnd = LongTimeHelper.endTime(curStart[0], curStart[1], curStart[2], curStart[3], curStart[4], curStart[5]);
        NvrService.getInstance().time2VideoPath(
                playBackParam.getIp()[0], playBackParam.getIp()[1],
                playBackParam.getIp()[2], playBackParam.getIp()[3],
                playBackParam.getPort(), playBackParam.getChannel(),
                curStart[0], curStart[1], curStart[2], curStart[3], curStart[4], curStart[5],
                curEnd[0], curEnd[1], curEnd[2], curEnd[3], curEnd[4], curEnd[5],
                tsDir + File.separator + "play" + index + ".dav",
                new PlayCallback() {
                    @Override
                    public void onComplete(String filePath) {
                        if (FileUtils.isExist(filePath)) {
                            String curM3U8 = sysSingleMp42TS(filePath, curIndex[0], tsDir);
                            appendM3U8(curM3U8, longTimeM3U8Path, tsIndex, tsDir);
                            FileUtils.rm(filePath);
                        } else {
                            // 可能接下来是一串的失败，放慢这种失败的速度
                            VSLog.e(TAG, "PLAYBACK FILE NOT EXIST:" + filePath);
                            VSLog.d(TAG, playBackParam.toString());
                            ThreadUtils.sleep(1000);
                        }
                    }
                });
        System.arraycopy(curEnd, 0, curStart, 0, curStart.length);
    }

    public static String sysSingleMp42TS(String tmpFilePath, int index, String tsDir) {
//        CmdExecutor.wait("ffmpeg -n -i " + tmpFilePath + " -s 640x360 -f ssegment -segment_format mpegts -segment_list "
//                + tsDir + "/play" + index + ".m3u8 -vcodec libx264 " + tsDir + "/out" + index + "%03d.ts");
        CmdExecutor.wait("ffmpeg -n -i " + tmpFilePath + " -s 640x360 -vcodec libx264 -hls_allow_cache 1 -hls_list_size 0 -f hls " + tsDir + "/play" + index + ".m3u8");
//        -segment_time 2
        return tsDir + "/play" + index + ".m3u8";
    }

    public static StringBuilder appendM3U8Line(StringBuilder buf, String lineContent, int[] curIndex, String tsDir) {
        if (!lineContent.equals("#EXT-X-ENDLIST")) {
            if (lineContent.startsWith("#")) {
                buf.append("#EXT-X-DISCONTINUITY\n");
                buf.append(lineContent);
                buf.append("\n");
            } else if (lineContent.startsWith("play")) {
                buf.append("play");
                buf.append(curIndex[0]);
                buf.append(".ts");
                buf.append("\n");
                FileUtils.rename(tsDir + File.separator + lineContent, tsDir + File.separator + "play" + curIndex[0] + ".ts");
                curIndex[0]++;
            }
        }
        return buf;
    }

    public static int firstM3U8(String partPath, String wholePath, final String tsDir) {
        final int[] tsIndex = new int[1];
        tsIndex[0] = 0;
        String buf = ("#EXTM3U\n" +
                "#EXT-X-VERSION:3\n" +
                "#EXT-X-MEDIA-SEQUENCE:0") +
//                seq +
                "\n" + "#EXT-X-ALLOW-CACHE:YES\n" + "#EXT-X-TARGETDURATION:12\n";
        FileUtils.overWriteFile(wholePath, buf);
        appendM3U8(partPath, wholePath, tsIndex, tsDir);
        FileUtils.rm(partPath);
        return tsIndex[0];
    }

    public static int appendM3U8(final String partPath, String wholePath, final int[] curIndex, final String tsDir) {
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

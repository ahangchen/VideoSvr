package cwh.web.model.realplay;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;
import cwh.web.session.SessionManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by cwh on 16-1-6
 */
public class M3U8Mng {
    public static String TAG = "M3U8Mng";

    private static class Holder {
        static final M3U8Mng instance = new M3U8Mng();
    }

    public static M3U8Mng getInstance() {
        return Holder.instance;
    }

    public static void startClean() {

    }

    public static int curTSNum(String curDir) {
        // 也可以考虑通过读取文件，排序来做
        String curM3U8Name = realPlayDir2Path(curDir);

        File file = new File(curM3U8Name);
        FileReader fileReader;
        BufferedReader reader = null;
        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            String tempString;
            //一次读一行，读入null时文件结束
            while ((tempString = reader.readLine()) != null) {
                if (!tempString.substring(0, 1).equals("#")) {
//                    VSLog.d(TAG, tempString + " " + Integer.parseInt(tempString.substring(1).split("\\.")[0]));
                    return Integer.parseInt(tempString.substring(1).split("\\.")[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassCastException e) {
            e.printStackTrace();
            //也可能M3U8文件里还没ts记录，发生空转int错误，返回-1
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        //读不到非#开头的行，-1代表全部保留
        return -1;
    }

    public static void validateM3U8(String m3u8Dir, Runnable onError, boolean[] stopValidate) {
        int lastNum = 0;
        int curNum;
        int invalidCount = 0;
        while (!stopValidate[0]) {
            curNum = curTSNum(m3u8Dir);
            if (lastNum == curNum) {
                invalidCount++;
            }
            lastNum = curNum;
            if (invalidCount > 10) {
                VSLog.d(TAG, "VALIDATE: cur:" + curNum + "; last:" + lastNum);
                onError.run();
                invalidCount = 0;
            }
            ThreadUtils.sleep(2000);
        }
    }

    public static void watchM3U8(final String m3u8Dir, final boolean[] stopValidate) {
        validateM3U8(m3u8Dir, new Runnable() {
            @Override
            public void run() {
                VSLog.e(TAG, "FFMEPG ERROR");
                replaceRealPlay(realPlayDir2Path(m3u8Dir));
            }
        }, stopValidate);
    }

    public static void replaceRealPlay(String videoPath) {
        RequestState requestState = SessionManager.getInstance().isCached(videoPath);
        String[] params = FileUtils.parentDirName(videoPath).split("-");
        String ip = params[0] + "." + params[1] + "." + params[2] + "." + params[3];
        String port = params[4];
        String channel = params[5];
        VSLog.d(TAG, "REPLACE realPlay: ip:" + ip + ", port: " + port + "; channel: " + channel);
        if (requestState != null) {
            ((RealPlayRes) requestState.getRes()).setConvertProcess(AsyncRealPlay.sysRealPlay(ip, port, channel));
        }
    }

    public static boolean waitForM3U8(String m3u8Path) {
        VSLog.d(TAG, m3u8Path);
        int i = 50; // counter
        while (i > 0) {
            // 存在即结束，不存在就sleep
            if (FileUtils.isExist(m3u8Path)) {
                return true;
            }
            VSLog.d(TAG, "NO EXIST");
            ThreadUtils.sleep(1000);
            i--;
        }
        return false;
    }

    public static boolean[] cleanToggle = new boolean[1];

    public static void timelyClean(String curPath, boolean[] stopClean) {
        ThreadUtils.sleep(5000);//一开始还不用工作，先睡会
        while (!stopClean[0] && FileUtils.isExist(curPath)
                && FileUtils.isExist(M3U8Mng.realPlayDir2Path(curPath))) {
            dueClean(curPath, curTSNum(curPath));
            ThreadUtils.sleep(2000);//不要清理太快, 省点cpu
        }
        VSLog.d(TAG, "stop timelyClean");
    }

    public static void globalTimelyClean(String globalPath, final boolean[] stopClean) {
        ThreadUtils.sleep(10000);
        while (!stopClean[0]) {
            FileUtils.flatTravel(globalPath, new FileUtils.Travel() {
                        @Override
                        public void onFile(File file) {
                            // 对realPlay目录下每个目录，做dueClean
                            if (file.isDirectory()) {
                                if (!stopClean[0] && FileUtils.isExist(file.getAbsolutePath())
                                        && FileUtils.isExist(M3U8Mng.realPlayDir2Path(file.getAbsolutePath()))) {
                                    dueClean(file.getAbsolutePath(), curTSNum(file.getAbsolutePath()));
                                }
                            }
                        }
                    }
            );
            ThreadUtils.sleep(2000);
        }
        VSLog.d(TAG, "STOP CLEAN");
    }


    // 通过读取m3u8文件的数据来决定删除的文件，可能会有文件并行读写问题，但效率高。
    public static void dueClean(String curPath, final int curRcd) {
        FileUtils.flatTravel(curPath, new FileUtils.Travel() {
            @Override
            public void onFile(File file) {
                int tsNum = getTsNum(file);
                if (curRcd == -1 || tsNum == -1) return;
                if (curRcd - 20 > tsNum) { // 宽松的删除条件，保留当前的ts之前的20个
//                    VSLog.d(TAG, "to delete");
                    file.delete();
                }
            }
        });
    }

    public static int getTsNum(File file) {
//        VSLog.d(TAG, file.getName());
        if (file.getName().equals("t.m3u8")) return -1;
        return Integer.parseInt(file.getName().substring(1).split("\\.")[0]);
    }

    public static String realPlayPath2Dir(String realPlayPath) {
        return realPlayPath.replace("/" + CommonDefine.REAL_PLAY_PATH + CommonDefine.M3U8, "");
    }

    public static String realPlayDir2Path(String realPlayDirPath) {
        return realPlayDirPath + "/" + CommonDefine.REAL_PLAY_PATH + CommonDefine.M3U8;
    }

    public static String realPlayDir(String ip, String port, String channel) {
        // 目录名只与摄像头相关
        String curDirPath = ip.replace(".", "-") + "-" + port + "-" + channel /* + "-"
                + DateUtils.formatCurDate()+"-"+ DateUtils.formatCurTime().replace(":","-")*/;
        File curDir = new File(CommonDefine.DATA_PATH + "/" + CommonDefine.REAL_PLAY_DIR_PATH + "/" + curDirPath);
        curDir.mkdir();
        return CommonDefine.DATA_PATH + "/" + CommonDefine.REAL_PLAY_DIR_PATH + "/" + curDirPath;
    }

    // 先拿到path
    public static String realPlayPath(String ip, String port, String channel) {
        return realPlayDir(ip, port, channel) + "/" + CommonDefine.REAL_PLAY_PATH + CommonDefine.M3U8;
    }


    public static void main(String[] args) {
//        String ip = "192.168.199.108";
//        String port = "554";
//        String channel = "1";
//// ffmpeg -i rtsp://admin:admin@192.168.1.108:554/cam/realmonitor?channel=1&subtype=0 -vcodec copy -f hls out.m3u8
//        final String realPlayVideoPath = M3U8Mng.realPlayPath(ip, port, channel);
//        // 在这里发起转换，然后把进程交给Session，等待前端传回终止信息或超时以终止这个进程
//        final Process convert = AsyncRealPlay.sysRealPlay(ip, port, channel, realPlayVideoPath);
    }
}

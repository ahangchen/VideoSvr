package cwh.NVR;

/**
 * Created by cwh on 15-11-28
 */

import cwh.NVR.play.PlayCallback;
import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.log.VSLog;
import cwh.utils.process.CmdExecutor;
import cwh.utils.socket.SocketServer;
import cwh.web.model.CommonDefine;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Nvr相关操作都放在这里调用
 * 对native层方法的封装与组合，简化上层操作
 * 全局多线程单例
 */
public class NvrService {
    public static String TAG = "NvrService";

    private static class Holder {
        public static final NvrService instance = new NvrService();
    }

    private ReentrantLock taskLock = new ReentrantLock();
    private int taskCnt = 0;

    public static NvrService getInstance() {
        return Holder.instance;
    }

    public void init() {
        NVRNative.init();
    }

    public void login() {
        NVRNative.login();
    }

    public void logout() {
        NVRNative.logout();
    }

    public void cleanUp() {
        NVRNative.cleanUp();
    }

    public void start() {
        init();
        login();
    }

    public void finish() {
        logout();
        cleanUp();
    }

    public void time2VideoPath(int ip0, int ip1, int ip2, int ip3, int port, int channel,
                               int startYear, int startMon, int startDay,
                               int startHour, int startMin, int startSec,
                               int endYear, int endMon, int endDay,
                               int endHour, int endMin, int endSec,
                               String videoPath, PlayCallback playCallback) {
        // wait for weak nvr, sync task Cnt
        boolean consume = false;
        int waitCount = 17; // 17 * 3 = 51,如果超过51秒还没等到,再转码就肯定超时了,不如结束掉自己,让机会给后来人
        while (waitCount > 0) {
            waitCount --;
            try {
                taskLock.lock();
                if (taskCnt < 9) {
                    taskCnt++;
                    consume = true;
                } else {
                    consume = false;
                }
            } finally {
                taskLock.unlock();
            }
            if (consume) {
                break;
            } else {
                VSLog.d(TAG, "wait because nvr full, current task Count: " + taskCnt);
                ThreadUtils.sleep(3000);
            }
        }
        CmdExecutor.wait(String.format("%s %d.%d.%d.%d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %s",
                CommonDefine.NVR_MNG_PATH,
                ip0, ip1, ip2, ip3, port, channel, startYear, startMon, startDay, startHour, startMin, startSec,
                endYear, endMon, endDay, endHour, endMin, endSec, videoPath));
        try {
            taskLock.lock();
            taskCnt --;
        } finally {
            taskLock.unlock();
        }
        playCallback.onComplete(videoPath);
    }

    public synchronized String getDevTime(String ip, String port) {
//
//        int[] iIps = StringUtils.str2Ips(ip);
//        int iPort = Integer.parseInt(port);
//        return NVRNative.getDevTime(iIps[0], iIps[1], iIps[2], iIps[3], iPort) + ".000";
        final String[] nativeResult = new String[1];
        ThreadUtils.runInBackGround(new Runnable() {
            @Override
            public void run() {
                nativeResult[0] = SocketServer.listenPort(CommonDefine.NATIVE_PORT);
            }
        });
        ThreadUtils.sleep(200); // 防止server还未开启
        CmdExecutor.wait(String.format("%s %s %s", CommonDefine.NVR_MNG_PATH, ip, port));
        VSLog.d(TAG, "device time: " + nativeResult[0]);
        return nativeResult[0];
    }

    public static void main(String[] args) {
//        VSLog.d(TAG, NvrService.getInstance().getDevTime("125.216.231.164", "37777"));
        ThreadUtils.runInBackGround(new Runnable() {
            @Override
            public void run() {
                VSLog.d(TAG, "233");
            }
        });
//        NvrService.getInstance().time2VideoPath(125, 216, 231, 164, 37777, 3, 2016, 4, 25, 9, 0, 0, 2016, 4, 25, 9, 0, 5, "/home/cwh/test.dav", new PlayCallback() {
//            @Override
//            public void onComplete(String filePath) {
//                VSLog.d(TAG, "TEST");
//            }
//        });
    }
}

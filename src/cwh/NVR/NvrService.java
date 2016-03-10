package cwh.NVR;

/**
 * Created by cwh on 15-11-28
 */

import cwh.NVR.play.PlayCallback;
import cwh.utils.StringUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;

import java.io.File;

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
        NVRNative.time2VideoPath(ip0, ip1,ip2, ip3, port, channel,
                startYear, startMon, startDay,
                startHour, startMin, startSec,
                endYear, endMon, endDay,
                endHour, endMin, endSec, videoPath,
                playCallback);
    }

    public String getDevTime(String ip, String port) {

        int[] iIps = StringUtils.str2Ips(ip);
        int iPort = Integer.parseInt(port);
        return NVRNative.getDevTime(iIps[0],iIps[1], iIps[2], iIps[3], iPort) + ".000";
    }

    public static void main(String[] args) {
        VSLog.d(TAG, NvrService.getInstance().getDevTime("125.216.231.168", "37777"));
    }
}

package cwh.web.model.realplay;

import cwh.NVR.NvrService;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.NvrInfo;

import java.io.IOException;

/**
 * Created by cwh on 16-1-7
 */
public class RealPlayRes {
    private NvrInfo nvrInfo;
    private String videoPath;
    private Process convertProcess;
    private boolean[] stopClean;
    public static String TAG = "RealPlayState";

    public RealPlayRes(String ip, String port, String videoPath, Process convertProcess, boolean[] stopClean) {
        nvrInfo = new NvrInfo(ip, port);
        this.videoPath = videoPath;
        this.convertProcess = convertProcess;
        this.stopClean = stopClean;
    }

    public NvrInfo getNvrInfo() {
        return nvrInfo;
    }

    public boolean[] getStopClean() {
        return stopClean;
    }

    public String getRealPlayPath() {
        return videoPath;
    }

    public Process getConvertProcess() {
        return convertProcess;
    }

    public void setConvertProcess(Process newReal) {
        convertProcess = newReal;
    }

//    public String getRealPlayFilePath() {
//        return getRealPlayPath() + "/" + CommonDefine.REAL_PLAY_PATH + CommonDefine.M3U8;
//    }


    public String toJson(String sid) {
        return "{" +
                "\"err\":\"0\"," +
                "\"sid\":\"" + sid +
                "\"," +
                "\"rpp\":\"" + getRealPlayPath().replace(CommonDefine.DATA_PATH + "/", "") +
                "\"," +
                "\"svrt\":\"" + NvrService.getInstance().getDevTime(getNvrInfo().getIp(), getNvrInfo().getPort()) +
                "\"}";
    }

    public static String cacheJson(String sid, String realPlayDirPath, String nvrIP, String nvrPort) {
        return "{" +
                "\"sid\":\"" + sid +
                "\"," +
                "\"rpp\":\"" + realPlayDirPath.replace(CommonDefine.DATA_PATH + "/", "") +
                "\"," +
                "\"svrt\":\"" + NvrService.getInstance().getDevTime(nvrIP, nvrPort) +
                "\"}";
    }

    public static void main(String[] args) {
    }
}

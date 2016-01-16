package cwh.web.model.realplay;

import cwh.NVR.NvrService;
import cwh.utils.date.DateUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;

import java.io.IOException;

/**
 * Created by cwh on 16-1-7
 */
public class RealPlayState extends RequestState{
    private String realPlayPath;
    private Process convertProcess;
    private boolean[] stopClean;
    public static String TAG = "RealPlayState";

    public RealPlayState(String realPlayPath, Process convertProcess, boolean[] stopClean) {
        this.realPlayPath = realPlayPath;
        this.convertProcess = convertProcess;
        this.stopClean = stopClean;

    }

    public boolean[] getStopClean() {
        return stopClean;
    }

    public String getRealPlayPath() {
        return realPlayPath;
    }

    public Process getConvertProcess() {
        return convertProcess;
    }

//    public String getRealPlayFilePath() {
//        return getRealPlayPath() + "/" + CommonDefine.REAL_PLAY_PATH + CommonDefine.M3U8;
//    }


    public String toJson(String sid) {
        return "{" +
                "\"sid\":\"" + sid +
                "\"," +
                "\"rpp\":\"" + getRealPlayPath().replace(CommonDefine.DATA_PATH + "/", "") +
                "\"," +
                "\"svrt\":\"" + NvrService.getInstance().getDevTime() +
                "\"}";
    }

    public static String cacheJson(String sid, String realPlayDirPath) {
        return "{" +
                "\"sid\":\"" + sid +
                "\"," +
                "\"rpp\":\"" + realPlayDirPath.replace(CommonDefine.DATA_PATH + "/", "") +
                "\"," +
                "\"svrt\":\"" + NvrService.getInstance().getDevTime() +
                "\"}";
    }

    public static void main(String[] args) {
        try {
            VSLog.d(TAG, new RealPlayState("/home/cwh", Runtime.getRuntime().exec("ls"), new boolean[1]).toJson("")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

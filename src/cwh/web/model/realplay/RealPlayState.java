package cwh.web.model.realplay;

import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;

import java.io.IOException;

/**
 * Created by cwh on 16-1-7
 */
public class RealPlayState {
    private String sessionId;
    private String realPlayDirPath;
    private Process convertProcess;
    private CleanToggle cleanToggle;

    public RealPlayState(String sessionId, String realPlayDirPath, Process convertProcess, CleanToggle cleanToggle) {
        this.sessionId = sessionId;
        this.realPlayDirPath = realPlayDirPath;
        this.convertProcess = convertProcess;
        this.cleanToggle = cleanToggle;
    }

    public CleanToggle getCleanToggle() {
        return cleanToggle;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRealPlayDirPath() {
        return realPlayDirPath;
    }

    public Process getConvertProcess() {
        return convertProcess;
    }

    public String getRealPlayFilePath() {
        return getRealPlayDirPath() + "/" + CommonDefine.rpFile + CommonDefine.M3U8;
    }

    public String toJson() {
        return "{" +
                "\"sid\":\"" + getSessionId() +
                "\"," +
                "\"rpp\":\"" + getRealPlayFilePath().replace(CommonDefine.dataPath + "/", "") +
                "\"}";
    }

    public static void main(String[] args) {
        try {
            VSLog.d(new RealPlayState("11", "/home/cwh", Runtime.getRuntime().exec("ls"), new CleanToggle()).toJson()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

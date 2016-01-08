package cwh.web.model.realplay;

import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;

import java.io.IOException;
import java.util.Date;

/**
 * Created by cwh on 16-1-7
 */
public class RealPlayState {
    private String sessionId;
    private String realPlayDirPath;
    private Process convertProcess;

    public RealPlayState(String sessionId, String realPlayDirPath, Process convertProcess) {
        this.sessionId = sessionId;
        this.realPlayDirPath = realPlayDirPath;
        this.convertProcess = convertProcess;
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
        return getRealPlayDirPath() + "/"+CommonDefine.rpFile + CommonDefine.M3U8;
    }

    public String toJson() {
        return "{" +
                "\"sid\":\""+ getSessionId() +
                "\"," +
                "\"rpp\":\"" + getRealPlayFilePath()+
                "\"}";
    }

    public String hashSession(){
        long seed = 131; // 31 131 1313 13131 131313 etc..  BKDRHash
        long hash=0;
        String value = getRealPlayDirPath() + new Date();
        for (int i = 0; i< value.length(); i++) {
            hash = (hash * seed) + value.charAt(i);
        }
        return String.valueOf(hash);
    }

    public static void main(String[]args) {
        try {
            VSLog.d( new RealPlayState("11", "/home/cwh", Runtime.getRuntime().exec("ls")).toJson()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

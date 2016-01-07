package cwh.web.model.realplay;

import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by cwh on 16-1-7
 */
public class RealplayState {
    private String session;
    private String realPlayDirPath;
    private Process convertProcess;

    public RealplayState(String session, String realPlayDirPath, Process convertProcess) {
        this.session = session;
        this.realPlayDirPath = realPlayDirPath;
        this.convertProcess = convertProcess;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getRealPlayDirPath() {
        return realPlayDirPath;
    }

    public void setRealPlayDirPath(String realPlayDirPath) {
        this.realPlayDirPath = realPlayDirPath;
    }

    public Process getConvertProcess() {
        return convertProcess;
    }

    public void setConvertProcess(Process convertProcess) {
        this.convertProcess = convertProcess;
    }

    public String getRealPlayFilePath() {
        return getRealPlayDirPath() + "/"+CommonDefine.rpFile + CommonDefine.M3U8;
    }

    public String toJson() {
        return "{" +
                "\"sid\":\""+getSession() +
                "\"," +
                "\"rpp\":\"" + getRealPlayDirPath()+
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
            VSLog.d( new RealplayState("11", "/home/cwh", Runtime.getRuntime().exec("ls")).toJson()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

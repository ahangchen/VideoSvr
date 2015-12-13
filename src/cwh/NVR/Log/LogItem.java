package cwh.NVR.Log;

/**
 * Created by cwh on 15-11-29
 */
public class LogItem {
    private String strLogType;
    private String strLogTime ;
    private String strLogContext;

    public LogItem(String type, String time, String context){
        strLogType = type;
        strLogTime = time;
        strLogContext = context;
    }

    public String getStrLogType() {
        return strLogType;
    }

    public void setStrLogType(String strLogType) {
        this.strLogType = strLogType;
    }

    public String getStrLogTime() {
        return strLogTime;
    }

    public void setStrLogTime(String strLogTime) {
        this.strLogTime = strLogTime;
    }

    public String getStrLogContext() {
        return strLogContext;
    }

    public void setStrLogContext(String strLogContext) {
        this.strLogContext = strLogContext;
    }
}

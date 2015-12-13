package cwh.NVR.Log;

import cwh.NVR.NVRNative;
import cwh.NVR.NvrService;

import java.util.ArrayList;

/**
 * Created by cwh on 15-11-29
 */
public class LogManager {
    //查询类型
    public enum DH_LOG_QUERY_TYPE {
        DHLOG_ALL,
        DHLOG_SYSTEM,
        DHLOG_CONFIG,
        DHLOG_STORAGE,
        DHLOG_ALARM,
        DHLOG_RECORD,
        DHLOG_ACCOUNT,
        DHLOG_CLEAR,
        DHLOG_PLAYBACK,
        DHLOG_MANAGER
    }
    private static class Holder {
        public static final LogManager instance = new LogManager();
    }

    public static LogManager getInstance() {
        return Holder.instance;
    }

    public ArrayList<LogItem> getLogs(DH_LOG_QUERY_TYPE type,
                                             int startYear, int startMon, int startDay, int startHour, int startMin, int startSec,
                                             int endYear, int endMon, int endDay, int endHour, int endMin, int endSec
    ) {
        return NVRNative.getLogs(type.ordinal(), startYear, startMon, startDay, startHour, startMin, startSec, endYear, endMon, endDay, endHour, endMin, endSec);
    }

    public void displayLogs(){
        NvrService.getInstance().start();

//        NvrService.getInstance().finish();
//        NvrService.getInstance().start();
        ArrayList<LogItem> logs = LogManager.getInstance().getLogs(LogManager.DH_LOG_QUERY_TYPE.DHLOG_ALL, 2015, 11, 17, 0, 0, 0, 2015, 11, 29, 0, 0, 0);
        for (LogItem log : logs) {
            System.out.println(log.getStrLogType());
            System.out.println(log.getStrLogTime());
            System.out.println(log.getStrLogContext());
            System.out.println("-----------------------");
        }
        NvrService.getInstance().finish();
    }
}

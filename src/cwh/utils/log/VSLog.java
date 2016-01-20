package cwh.utils.log;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.date.DateUtils;
import cwh.web.model.CommonDefine;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by cwh on 16-1-2
 */
public class VSLog {
    public static String DEBUG_LOG_PATH = "/media/Software/lab/data/";
    public static String POWERFUL_LOG_PATH = "/home/cwh/software/tomcat/webapps/VideoSvr/log/";
    public static String logPath = CommonDefine.DEBUG ? DEBUG_LOG_PATH : POWERFUL_LOG_PATH;
    public static String EXT = ".log";
    public static String DEBUG = "[D]";
    public static String ERROR = "[E]";

    public static boolean LOG = true;

    public static void d(String TAG, String logStr) {
        log(TAG, DEBUG, logStr);
    }

    public static void e(String tag, String logStr) {
        log(tag, ERROR, logStr);
    }

    public static void e(String tag, String logStr, Throwable t) {
        err(tag, logStr, t);
    }

    private static void printLog(String logFileName, String formatLog) {
        try {
            FileOutputStream out = new FileOutputStream(new File(logPath + logFileName), true);
            if (LOG) out.write(formatLog.getBytes());
            System.out.print(formatLog);
            out.close();
        } catch (Exception e) {
            System.out.println();
        }
    }

    public static void log(String tag, String type, String logStr) {
        String logName = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1)
                + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + EXT;
        String log = format(tag, logStr, type);

        printLog(logName, log);
    }


    private static String format(String tag, String log, String type) {
        return DateUtils.formatCurTime() + " "
                + type + " : " + "[" + tag + "]\t\t"
                + log + "\n";
    }

    private static String format(String log, String type) {
        return "          "+DateUtils.formatCurTime() + " "
                + type
                + " : " + log + "\n";
    }

    private static void printErr(String logFileName, String formatLog, Throwable throwable) {
        try {
            FileOutputStream out = new FileOutputStream(new File(logPath + logFileName), true);
            if (LOG) out.write(formatLog.getBytes());
            System.out.print(formatLog);
            StackTraceElement[] stacks = throwable.getStackTrace();
            for (StackTraceElement stack : stacks) {
                if (LOG) out.write(format(stack.toString(), ERROR).getBytes());
                System.out.print(format(stack.toString(), ERROR));
            }
            out.close();
        } catch (Exception e) {
            System.out.println();
        }
    }

    public static void err(String tag, String logStr, Throwable throwable) {
        String logName = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1)
                + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + EXT;
        String log = format(tag, logStr, ERROR);
        printErr(logName, log, throwable);
    }

    public static void main(String[] args) {
        log("Vslog", DEBUG, "TEST");
        ThreadUtils.sleep(200);
        log("VSLOG", ERROR, "TEST");
    }
}

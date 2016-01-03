package cwh.utils.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

/**
 * Created by cwh on 16-1-2
 */
public class VSLog {
    public static String logPath = "/media/Software/lab/data/";
    public static String EXT = ".log";
    public static String DEBUG = "[D]";
    public static String ERROR = "[E]";
    public static String WARN = "[W]";

    public static void log(String type, String logStr) {
        String logName = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1)
                + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + EXT;
        String log = format(logStr, type);
        try {
            FileOutputStream out = new FileOutputStream(new File(logPath + logName), true);
            out.write(log.getBytes());
            out.close();
        } catch (Exception e) {

        }
    }

    private static String format(String log, String type) {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + (Calendar.getInstance().get(Calendar.MINUTE))
                + ":" + Calendar.getInstance().get(Calendar.SECOND) + " "
                + type
                + " : " + log + "\n";
    }

    public static void err(String logStr, Throwable throwable) {
        String logName = Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1)
                + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + EXT;
        String log = format(logStr, ERROR);
        try {
            FileOutputStream out = new FileOutputStream(new File(logPath + logName), true);
            out.write(log.getBytes());
            StackTraceElement[] stacks = throwable.getStackTrace();
            for (int i = 0; i < stacks.length; i++) {
                out.write(format(stacks[i].toString(), ERROR).getBytes());
            }
            out.close();
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        log(DEBUG, "TEST");
        err("TEST ERROR", new NullPointerException());
    }
}

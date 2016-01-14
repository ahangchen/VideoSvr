package cwh.utils.date;

import java.util.Calendar;

/**
 * Created by cwh on 16-1-5
 */
public class DateUtils {
    public static String formatCurDate() {
        return String.format("%04d-%02d-%02d", Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    public static String formatCurTime() {
        return String.format("%02d:%02d:%02d.%03d",
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                Calendar.getInstance().get(Calendar.SECOND),
                Calendar.getInstance().get(Calendar.MILLISECOND));
    }

    public static String formatCurDateTime() {
        return formatCurDate() + "-" + formatCurTime();
    }
}

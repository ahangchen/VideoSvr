package cwh.utils.date;

import java.util.Calendar;

/**
 * Created by cwh on 16-1-5
 */
public class DateUtils {
    public static String formatCurDate() {
        return Calendar.getInstance().get(Calendar.YEAR)
                + "-" + Calendar.getInstance().get(Calendar.MONTH)
                + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static String formatCurTime() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                + ":" + Calendar.getInstance().get(Calendar.MINUTE)
                + ":" + Calendar.getInstance().get(Calendar.SECOND);
    }
}

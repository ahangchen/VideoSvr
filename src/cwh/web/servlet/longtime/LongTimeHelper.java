package cwh.web.servlet.longtime;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.date.DateUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.longtime.AsyncLongTimePlay;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

/**
 * Created by cwh on 16-3-9
 */
public class LongTimeHelper {
    public static String TAG = "LongTimeHelper";

    public static void asyncResponse(HttpServletRequest request, AsyncListener asyncListener) {
        VSLog.d(TAG, "playback asyncResponse");
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(60 * 1000);
        asyncContext.addListener(asyncListener);
        ThreadUtils.runInBackGround(new AsyncLongTimePlay(asyncContext));
    }

    public static int[][][] splitTime(int startYear, int startMonth, int startDay, int startHour, int startMin, int startSec,
                                      int endYear, int endMonth, int endDay, int endHour, int endMin, int endSec) {
        Calendar startDate = DateUtils.genTime(startYear, startMonth, startDay, startHour, startMin, startSec);
        Calendar endDate = DateUtils.genTime(endYear, endMonth, endDay, endHour, endMin, endSec);
        long duration = endDate.getTimeInMillis() - startDate.getTimeInMillis();
        long mod = duration % CommonDefine.INTERVAL;
        long cnt = duration / CommonDefine.INTERVAL;
        int len = (int) cnt + (mod == 0 ? 0 : 1);
        int[][][] sps = new int[len][2][6];
        for (int i = 0; i < cnt; i++) {
            sps[i][0][0] = startDate.get(Calendar.YEAR);
            sps[i][0][1] = startDate.get(Calendar.MONTH);
            sps[i][0][2] = startDate.get(Calendar.DAY_OF_MONTH);
            sps[i][0][3] = startDate.get(Calendar.HOUR_OF_DAY);
            sps[i][0][4] = startDate.get(Calendar.MINUTE);
            sps[i][0][5] = startDate.get(Calendar.SECOND);
            startDate.setTimeInMillis(startDate.getTimeInMillis() + CommonDefine.INTERVAL);
            sps[i][1][0] = startDate.get(Calendar.YEAR);
            sps[i][1][1] = startDate.get(Calendar.MONTH);
            sps[i][1][2] = startDate.get(Calendar.DAY_OF_MONTH);
            sps[i][1][3] = startDate.get(Calendar.HOUR_OF_DAY);
            sps[i][1][4] = startDate.get(Calendar.MINUTE);
            sps[i][1][5] = startDate.get(Calendar.SECOND);
        }
        if (mod != 0) {
            sps[len - 1][0][0] = startDate.get(Calendar.YEAR);
            sps[len - 1][0][1] = startDate.get(Calendar.MONTH);
            sps[len - 1][0][2] = startDate.get(Calendar.DAY_OF_MONTH);
            sps[len - 1][0][3] = startDate.get(Calendar.HOUR_OF_DAY);
            sps[len - 1][0][4] = startDate.get(Calendar.MINUTE);
            sps[len - 1][0][5] = startDate.get(Calendar.SECOND);
            startDate.setTimeInMillis(startDate.getTimeInMillis() + mod);
            sps[len - 1][1][0] = startDate.get(Calendar.YEAR);
            sps[len - 1][1][1] = startDate.get(Calendar.MONTH);
            sps[len - 1][1][2] = startDate.get(Calendar.DAY_OF_MONTH);
            sps[len - 1][1][3] = startDate.get(Calendar.HOUR_OF_DAY);
            sps[len - 1][1][4] = startDate.get(Calendar.MINUTE);
            sps[len - 1][1][5] = startDate.get(Calendar.SECOND);
        }
        return sps;
    }

    public static void main(String[] args) {
        int[][][] timeIntervals = splitTime(2016, 3, 5, 0, 0, 0, 2016, 3, 5, 0, 1, 5);
        for (int[][] timeInterval : timeIntervals) {
            VSLog.d(TAG, "" + timeInterval[0][0] + "-" + timeInterval[0][1] + "-" + timeInterval[0][2]
                    + " " + timeInterval[0][3]+ ":" + timeInterval[0][4]+ ":" + timeInterval[0][5]);
            VSLog.d(TAG, "" + timeInterval[1][0] + "-" + timeInterval[1][1] + "-" + timeInterval[1][2]
                    + " " + timeInterval[1][3]+ ":" + timeInterval[1][4]+ ":" + timeInterval[1][5]);
        }
    }


}

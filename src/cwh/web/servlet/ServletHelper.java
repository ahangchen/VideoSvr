package cwh.web.servlet;

import cwh.utils.StringUtils;
import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.playback.AsyncQueryVideo;
import cwh.web.servlet.longtime.LongTimeHelper;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by cwh on 15-12-13
 */
public class ServletHelper {
    public static String TAG = "ServletHelper";
    public static String REGX_PLAYBACK_DATE_TIME = "^\\d{4}-(\\d{2}|\\d{1})-(\\d{2}|\\d{1})-(\\d{1}|\\d{2})-(\\d{1}|\\d{2})-(\\d{1}|\\d{2})$";
    public static String REGX_SID = "^[A-Za-z0-9]+$";

    public static void responseString(ServletResponse response, String stringResponse) {
        VSLog.d(TAG, stringResponse);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println(stringResponse);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static boolean isParamOk(HttpServletRequest request) {
        // start=2015-12-11-0-0-0&end=2015-12-11-0-0-3&channel=0&sid=131212121
        if (!LongTimeHelper.isParamOk(request)) {
            return false;
        }
        String end = request.getParameter(CommonDefine.END);
        if (StringUtils.isEmpty(end)) {
            return false;
        }
        if (!StringUtils.isMatch(end, REGX_PLAYBACK_DATE_TIME)) {
            VSLog.e(TAG, "start time illegal :" + request.getQueryString());
            return false;
        }
        return true;
    }

    public static String genErrCode(int errCode, String detail) {
        return String.format("{\"err\":\"%d\",\"msg\":\"%s\"}", errCode, detail);
    }

    public static void main(String[] args) {
        String playbackDateTime = "^\\d{4}-(\\d{2}|\\d{1})-(\\d{2}|\\d{1})-(\\d{1}|\\d{2})-(\\d{1}|\\d{2})-(\\d{1}|\\d{2})$";
        VSLog.d(TAG, StringUtils.isMatch("2015-12-11-0-01-0", playbackDateTime) + "");
    }
}

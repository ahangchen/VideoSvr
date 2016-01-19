package cwh.web.servlet.playback;

import cwh.utils.StringUtils;
import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.playback.AsyncQueryVideo;

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
public class PlaybackHelper {
    public static String TAG = "PlayBackHelper";
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

    public static void asyncResponse(HttpServletRequest request, AsyncListener asyncListener) {
        VSLog.d(TAG, "playback asyncResponse");
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(60 * 1000);
        asyncContext.addListener(asyncListener);
        ThreadUtils.runInBackGround(new AsyncQueryVideo(asyncContext));
    }

    public static boolean isParamOk(HttpServletRequest request) {
        // start=2015-12-11-0-0-0&end=2015-12-11-0-0-3&channel=0&sid=131212121
        String start = request.getParameter(CommonDefine.START);
        if (StringUtils.isEmpty(start)) {
            return false;
        }
        if(!StringUtils.isMatch(start, REGX_PLAYBACK_DATE_TIME)) {
            VSLog.e(TAG, "start time illegal :" + request.getQueryString());
            return false;
        }
        String end = request.getParameter(CommonDefine.END);
        if (StringUtils.isEmpty(end)) {
            return false;
        }
        if(!StringUtils.isMatch(end, REGX_PLAYBACK_DATE_TIME)) {
            VSLog.e(TAG, "start time illegal :" + request.getQueryString());
            return false;
        }
        String sid = request.getParameter(CommonDefine.SID);
        if (!StringUtils.isEmpty(sid)) {
            if (!StringUtils.isMatch(sid, REGX_SID)){
                VSLog.e(TAG, "sid illegal :" + request.getQueryString());
                return false;
            }
        }
        return true;
    }

    public static void main(String[]args) {
        String playbackDateTime = "^\\d{4}-(\\d{2}|\\d{1})-(\\d{2}|\\d{1})-(\\d{1}|\\d{2})-(\\d{1}|\\d{2})-(\\d{1}|\\d{2})$";
        VSLog.d(TAG, StringUtils.isMatch("2015-12-11-0-01-0", playbackDateTime)+"");
    }
}

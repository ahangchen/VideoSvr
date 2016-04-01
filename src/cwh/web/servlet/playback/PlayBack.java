package cwh.web.servlet.playback;

import cwh.utils.log.VSLog;
import cwh.web.model.QueryVideoListener;
import cwh.web.servlet.ServletHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cwh on 15-12-13
 * 这个类主要做调度用，包括request的get，response的set，其他model的调用
 */
@WebServlet(name = "Playback", asyncSupported = true)
public class PlayBack extends HttpServlet{
    // http://localhost:8888/VideoSvr/Playback?start=2015-12-11-0-0-0&end=2015-12-11-0-0-3&channel=0&sid=131212121
    // return {"sid":"1154234445","rpp":"/home/cwh"}
    public static String TAG = "PlayBack";
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        VSLog.d(TAG, "Playback param:" + request.getQueryString());
        if (ServletHelper.isParamOk(request)) {
            PlaybackHelper.asyncResponse(request, new QueryVideoListener());
        } else {
            ServletHelper.responseString(response, ServletHelper.genErrCode(1, "param illegal " + request.getQueryString()));
        }

    }
}

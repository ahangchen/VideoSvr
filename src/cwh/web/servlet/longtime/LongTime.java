package cwh.web.servlet.longtime;

import cwh.utils.log.VSLog;
import cwh.web.model.QueryVideoListener;
import cwh.web.servlet.playback.PlaybackHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cwh on 16-3-9
 */
@WebServlet(name = "LongTime", asyncSupported = true)
public class LongTime extends HttpServlet{
    public static String TAG = "LongTime";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        VSLog.d(TAG, "Playback param:" + request.getQueryString());
        if (PlaybackHelper.isParamOk(request)) {
            LongTimeHelper.asyncResponse(request, new QueryVideoListener());
        } else {
            PlaybackHelper.responseString(response, "param illegal " + request.getQueryString());
        }
    }
}

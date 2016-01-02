package cwh.web.servlet;

import cwh.utils.log.VSLog;
import cwh.web.model.AsyncQueryVideo;

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
    public static void responseString(ServletResponse response, String stringResponse) {
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
        VSLog.log(VSLog.DEBUG, "playback asyncResponse");
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(60 * 1000);
        asyncContext.addListener(asyncListener);
        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        executor.execute(new AsyncQueryVideo(asyncContext));
    }
}

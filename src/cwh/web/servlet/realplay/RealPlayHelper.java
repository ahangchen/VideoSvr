package cwh.web.servlet.realplay;

import cwh.web.model.realplay.AsyncRealPlay;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by cwh on 16-1-2
 */
public class RealPlayHelper {
    public static void asyncResponse(HttpServletRequest request, AsyncListener asyncListener) {
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(60 * 1000);
        asyncContext.addListener(asyncListener);
        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        executor.execute(new AsyncRealPlay(asyncContext));
    }
}

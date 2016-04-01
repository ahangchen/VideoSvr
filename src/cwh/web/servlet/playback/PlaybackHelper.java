package cwh.web.servlet.playback;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.playback.AsyncQueryVideo;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by cwh on 16-3-30.
 */
public class PlaybackHelper {
    public static String TAG = "PlaybackHelper";
    public static void asyncResponse(HttpServletRequest request, AsyncListener asyncListener) {
        VSLog.d(TAG, "playback asyncResponse");
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(60 * 1000);
        asyncContext.addListener(asyncListener);
        ThreadUtils.runInBackGround(new AsyncQueryVideo(asyncContext));
    }
}

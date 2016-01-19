package cwh.web.model;

import cwh.utils.log.VSLog;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;

/**
 * Created by cwh on 15-12-14
 */
public class QueryVideoListener implements AsyncListener{
    public static String TAG = "QueryVideoListener";
    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {

    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        VSLog.e(TAG, "async query time out");
    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        VSLog.e(TAG, "async query error");
    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

    }
}

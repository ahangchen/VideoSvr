package cwh.NVR.play;

import cwh.utils.log.VSLog;

/**
 * Created by cwh on 15-12-14
 */
public class PlayCallbackImpl implements PlayCallback {
    public static String TAG = "PlayCallback";
    @Override
    public void onComplete(String filePath) {
        VSLog.d(TAG, "Java onComplete :" + filePath);
    }

}

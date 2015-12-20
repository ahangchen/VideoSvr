package cwh.NVR.play;

/**
 * Created by cwh on 15-12-14.
 */
public class PlayCallbackImpl implements PlayCallback {
    @Override
    public void onComplete(String filePath) {
        System.out.print("Java onComplete :" + filePath);
    }

}

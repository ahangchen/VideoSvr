package cwh.NVR;

/**
 * Created by cwh on 15-11-28
 */

import cwh.NVR.Log.LogItem;
import cwh.NVR.play.PlayCallback;
import cwh.web.model.CommonDefine;

import java.util.ArrayList;

/**
 * 1.use jni,call nvr method
 *
 * 2.create cpp file.
 * javah -d lib -classpath out/production/VideoSvr -jni cwh.NVR.NVRNative
 * 3.build .so:
 *      one way:
 *          gcc -fPIC -D_REENTRANT -I /home/cwh/java/include -I /home/cwh/java/include/linux -c lib/cwh_NVR_NVRNative.c
 *          gcc -shared cwh_NVR_NVRNative.o -o libNVR.so
 *      second way:
 *          build in clion, produce a so file for java.
 *  4.keep native.h in lib directory for future modify.
 *  5.to call java method in native code, you should get signature for a method
 *      cd out/production/VideoSvr
 *      javap -s -p cwh.NVR.Log.LogItem
 */
public class NVRNative {

    static {
        System.load(CommonDefine.LIB_PATH);
    }

    public static native void login();
    public static native void init();
    public static native void logout();
    public static native void cleanUp();

    public static native ArrayList<LogItem> getLogs(int type,
                                                    int startYear, int startMon, int startDay, int startHour, int startMin, int startSec,
                                                    int endYear, int endMon, int endDay, int endHour, int endMin, int endSec);
    public static native void time2VideoPath (int ip0, int ip1, int ip2, int ip3, int port, int channel ,
                                              int startYear, int startMon, int startDay, int startHour, int startMin, int startSec,
                                              int endYear, int endMon, int endDay, int endHour, int endMin, int endSec, String videoDirPath,
                                              PlayCallback playCallback);

    public static native String getDevTime(int ip0, int ip1, int ip2, int ip3, int port);
}

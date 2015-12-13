package cwh.NVR;

/**
 * Created by cwh on 15-11-28
 */

import cwh.NVR.Log.LogItem;

import java.util.ArrayList;

/**
 * 1.use jni,call nvr method
 *  javah -classpath out/production/VideoSvr -d lib -jni cwh.NVR.NVRNative
 * 2.create cpp file.
 * 3.build .so:
 *      one way:
 *          gcc -fPIC -D_REENTRANT -I /home/cwh/java/include -I /home/cwh/java/include/linux -c lib/cwh_NVR_NVRNative.c
 *          gcc -shared cwh_NVR_NVRNative.o -o libNVR.so
 *      second way:
 *          build in clion, produce a so file for java.
 *  4.keep native.h in lib directory for future modify.
 *  5.to call java method in native code, you should get signature for a method
 *      cd out/production/SecSvr
 *      javap -s -p cwh.NVR.Log.LogItem
 */
public class NVRNative {
    static {
        System.load("/home/cwh/coding/VideoSvr/lib/libnvr.so");
    }

    public static native void login();
    public static native void init();
    public static native void logout();
    public static native void cleanUp();

    public static native ArrayList<LogItem> getLogs(int type,
                                                    int startYear, int startMon, int startDay, int startHour, int startMin, int startSec,
                                                    int endYear, int endMon, int endDay, int endHour, int endMin, int endSec);
}

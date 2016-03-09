package cwh.web.model;

/**
 * Created by cwh on 16-1-5
 */
public class CommonDefine {

    // deploy
    public static boolean DEBUG = true;
    public static boolean POWER = !DEBUG;

    // file
    public static String DEBUG_DATA_PATH = "/home/cwh/tomcat/webapps/VideoSvr/videos";
    public static String POWER_DATA_PATH = "/home/cwh/software/tomcat/webapps/VideoSvr/videos";
    public static String HADOOP_DATA_PATH = "/home/hadoop/software/tomcat/webapps/VideoSvr/videos";

    public static String DEBUG_LIB_PATH = "/home/cwh/coding/VideoSvr/lib/libnvr.so";
    public static String POWER_LIB_PATH = "/home/cwh/software/tomcat/webapps/VideoSvr/lib/libnvr.so";
    public static String HADOOP_LIB_PATH = "/home/hadoop/software/tomcat/webapps/VideoSvr/lib/libnvr.so";
    public static String LIB_PATH =
            CommonDefine.DEBUG
                    ? DEBUG_LIB_PATH
                    : (CommonDefine.POWER
                    ? POWER_LIB_PATH
                    : HADOOP_LIB_PATH);

    public static String DATA_PATH =
            CommonDefine.DEBUG
                    ? DEBUG_DATA_PATH
                    : (CommonDefine.POWER
                    ? POWER_DATA_PATH
                    : HADOOP_DATA_PATH);
    public static String REAL_PLAY_DIR_PATH = "realplay";
    public static String PLAY_BACK_DIR_PATH = DATA_PATH + "/playback";
    public static String REAL_PLAY_PATH = "t";

    // device
    public static String USER = "admin";
    public static String PWD = "admin";

    // media
    public static String TMP_SUFF = "-tmp.dav";
    public static String M3U8 = ".m3u8";
    public static String MP4 = ".mp4";

    // ffmpeg
    public static String FFMPEG_CONVERT = "ffmpeg -i rtsp://" + CommonDefine.USER + ":" + CommonDefine.PWD
            + "@%s:%s"
            + "/cam/realmonitor?"
            + "channel=%s&subtype=0 "
            + "-vcodec copy -f hls %s -rtsp_transport tcp";

    // query
    public static String IP = "ip";
    public static String PORT = "port";
    public static String NVR_IP = "nip";
    public static String NVR_PORT = "nport";
    public static String SID = "sid";
    public static String CHANNEL = "channel";
    public static String START = "start";
    public static String END = "end";

    //session
    public static int SESSION_INVALID_INTERVAL = 30; // min
    public static int DELAY = SESSION_INVALID_INTERVAL * 60;// s
    public static int TOUCH_INTERVAL = 20 * 60 * 1000; // ms
}

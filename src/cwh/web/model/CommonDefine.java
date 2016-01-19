package cwh.web.model;

/**
 * Created by cwh on 16-1-5
 */
public class CommonDefine {
    // file
    public static String DATA_PATH = "/home/cwh/tomcat/webapps/VideoSvr/videos";
    public static String REAL_PLAY_DIR_PATH = "realplay";
    public static String PLAY_BACK_DIR_PATH = DATA_PATH + "/playback";
    public static String REAL_PLAY_PATH = "t";

    // device
    public static String USER = "admin";
    public static String PWD = "admin";

    // media
    public static String M3U8 = ".m3u8";
    public static String MP4 = ".mp4";

    // ffmpeg
    public static String FFMPEG_CONVERT = "ffmpeg -i rtsp://" + CommonDefine.USER + ":" + CommonDefine.PWD
            + "@%s:%s"
            + "/cam/realmonitor?"
            + "channel=%s&subtype=0 "
            + "-vcodec copy -f hls %s -rtsp_transport tcp"
            ;

    // query
    public static String IP = "ip";
    public static String PORT = "port";
    public static String NVR_IP = "nip";
    public static String NVR_PORT = "nport";
    public static String SID = "sid";
    public static String CHANNEL = "channel";
    public static String START = "start";
    public static String END = "end";
}

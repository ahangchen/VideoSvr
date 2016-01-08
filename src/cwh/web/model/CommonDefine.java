package cwh.web.model;

/**
 * Created by cwh on 16-1-5
 */
public class CommonDefine {
    // file
    public static String dataPath = "/home/cwh/Mission/lab/data/videoweb";
    public static String realPlayDirPath = "realplay";
    public static String playBackDirPath = dataPath + "/playback";
    public static String rpFile = "t";

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
            + "-vcodec copy -f hls %s"
            ;

    // query
    public static String IP = "ip";
    public static String PORT = "port";
    public static String SID = "sid";
    public static String CHANNEL = "channel";
    public static String START = "start";
    public static String END = "end";
}

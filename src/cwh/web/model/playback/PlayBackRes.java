package cwh.web.model.playback;

/**
 * Created by cwh on 16-1-7
 */
public class PlayBackRes {
    private String playFilePath;
    public PlayBackRes(String playFilePath) {
        this.playFilePath = playFilePath;
    }

    public String getPlaybackPath() {
        return playFilePath;
    }

    public void setPlaybackPath(String videoPath) {
        this.playFilePath = videoPath;
    }


    public String toJson(String sid) {
        return "{" +
                "\"err\":\"0\"," +
                "\"sid\":\"" + sid +
                "\"," +
                "\"rpp\":\"" + getPlaybackPath() +
                "\"}";
    }

}

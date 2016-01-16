package cwh.web.model.playback;

import cwh.web.model.RequestState;

/**
 * Created by cwh on 16-1-7
 */
public class PlaybackState extends RequestState {
    private String playFilePath;

    public PlaybackState(String playFilePath) {
        this.playFilePath = playFilePath;
    }

    public String getPlayFilePath() {
        return playFilePath;
    }

    public void setPlayFilePath(String playFilePath) {
        this.playFilePath = playFilePath;
    }


    public String toJson(String sid) {
        return "{" +
                "\"sid\":\"" + sid +
                "\"," +
                "\"rpp\":\"" + getPlayFilePath() +
                "\"}";
    }

}

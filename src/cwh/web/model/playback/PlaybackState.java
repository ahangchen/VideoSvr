package cwh.web.model.playback;

import java.util.Date;

/**
 * Created by cwh on 16-1-7
 */
public class PlaybackState {
    private String sessionId;
    private String playFilePath;

    public PlaybackState(String sessionId, String playFilePath) {
        this.sessionId = sessionId;
        this.playFilePath = playFilePath;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPlayFilePath() {
        return playFilePath;
    }

    public void setPlayFilePath(String playFilePath) {
        this.playFilePath = playFilePath;
    }


    public String toJson() {
        return "{" +
                "\"sid\":\"" + getSessionId() +
                "\"," +
                "\"rpp\":\"" + getPlayFilePath() +
                "\"}";
    }

}

package cwh.web.model.playback;

import java.util.Date;

/**
 * Created by cwh on 16-1-7
 */
public class PlaybackState {
    private String sessionId;
    private String playFilePath;

    public PlaybackState(String playFilePath) {
        this.sessionId = hashSession();
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

    public String hashSession(){
        long seed = 131; // 31 131 1313 13131 131313 etc..  BKDRHash
        long hash=0;
        String value = playFilePath + new Date();
        for (int i = 0; i< value.length(); i++) {
            hash = (hash * seed) + value.charAt(i);
        }
        return String.valueOf(hash);
    }
}

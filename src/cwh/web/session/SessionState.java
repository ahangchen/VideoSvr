package cwh.web.session;

import cwh.web.model.playback.PlaybackState;
import cwh.web.model.realplay.RealPlayState;

import java.util.LinkedList;

/**
 * Created by cwh on 16-1-7
 */
public class SessionState {
    private String sessionId;
    private LinkedList<PlaybackState> playbackStates = new LinkedList<PlaybackState>();
    private LinkedList<RealPlayState> realPlayStates = new LinkedList<RealPlayState>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LinkedList<PlaybackState> getPlaybackStates() {
        return playbackStates;
    }


    public LinkedList<RealPlayState> getRealPlayStates() {
        return realPlayStates;
    }

    public void addPlayback(PlaybackState playbackState) {
        playbackStates.add(playbackState);
    }
    public void addRealPlay(RealPlayState realPlayState) {
        realPlayStates.add(realPlayState);
    }
}

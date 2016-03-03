package cwh.web.session;

import cwh.web.model.RequestState;

import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cwh on 16-1-7
 */
public class SessionState {
    private String sessionId;
    private HttpSession session;

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    private LinkedList<RequestState> requestStates = new LinkedList<RequestState>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LinkedList<RequestState> getRequestStates() {
        return requestStates;
    }

    public void addRequest(RequestState requestState) {
        this.requestStates.add(requestState);
    }

    public void removeRequest(RequestState requestState) {
        requestStates.remove(requestState);
    }

    private Lock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public void unLock() {
        lock.unlock();
    }
}

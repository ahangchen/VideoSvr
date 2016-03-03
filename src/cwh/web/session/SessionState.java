package cwh.web.session;

import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.model.RequestState;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cwh on 16-1-7
 */
public class SessionState {
    private HttpSession session;
    private long lastTouchTime;

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
        lastTouchTime = new Date().getTime();
    }

    public void touch() {
        long cur = new Date().getTime();
        if(cur - lastTouchTime > CommonDefine.TOUCH_INTERVAL) {
            lastTouchTime = cur;
            session.setMaxInactiveInterval(session.getMaxInactiveInterval() + CommonDefine.DELAY);
            VSLog.d("Touch", "touch session: " + session.getId());
        } else {
            VSLog.d("Touch", "touch too frequently: " + session.getId());
        }
    }

    private LinkedList<RequestState> requestStates = new LinkedList<RequestState>();

    public String getSessionId() {
        return session.getId();
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

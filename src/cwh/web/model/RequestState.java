package cwh.web.model;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cwh on 16-1-13
 * 每个requestState与一个正在写的文件对应
 */
public class RequestState {
    protected String videoPath;
    protected LinkedList<String> attachSessions = new LinkedList<String>();

    public RequestState(String videoPath){
        this.videoPath = videoPath;
    }

    public boolean isAttached(){
        return !attachSessions.isEmpty();
    }

    public boolean contain(String sid) {
        return attachSessions.contains(sid);
    }

    public void addSession(String sid) {
        attachSessions.add(sid);
    }

    public void removeSession(String sid) {
        attachSessions.remove(sid);
    }

    public boolean isResExist() {
        return resExist;
    }

    public void setResExist(boolean resExist) {
        this.resExist = resExist;
    }

    protected boolean resExist = false;

    public void lock() {
        resLock.lock();
    }

    public void unLock() {
        resLock.unlock();
    }

    protected Lock resLock = new ReentrantLock();

    private Object res;

    public Object getRes() {
        return res;
    }

    public void setRes(Object res) {
        this.res = res;
    }
}

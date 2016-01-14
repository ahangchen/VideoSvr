package cwh.web.model;

import java.util.LinkedList;

/**
 * Created by cwh on 16-1-13
 * 每个requestState与一个正在写的文件对应
 */
public class RequestState {
    protected LinkedList<String> attachSessions = new LinkedList<String>();

    public LinkedList<String> getAttachSessions() {
        return attachSessions;
    }

    public void addSession(String sid) {
        attachSessions.add(sid);
    }

    public void removeSession(String sid) {
        attachSessions.remove(sid);
    }
}

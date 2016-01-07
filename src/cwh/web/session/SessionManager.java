package cwh.web.session;

import cwh.utils.VMath;

import javax.servlet.ServletContext;

/**
 * Created by cwh on 16-1-7
 */
public class SessionManager {
    private static class Holder {
        static final SessionManager instance = new SessionManager();
    }

    public static SessionManager getInstance() {
        return Holder.instance;
    }

}

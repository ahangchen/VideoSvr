package cwh.web.servlet;

import cwh.utils.log.VSLog;
import cwh.web.session.SessionManager;
import cwh.web.session.SessionState;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cwh on 16-2-28
 */
public class Login extends HttpServlet {
    public static String TAG = "Login";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionState sessionState = SessionManager.getInstance().getSessionState(req);
        String sidJson = "{ \"err\":\"0\",\"sid\":\"" + sessionState.getSessionId() + "\"}";
        VSLog.d(TAG, sidJson);
        ServletHelper.responseString(resp, sidJson);
    }
}

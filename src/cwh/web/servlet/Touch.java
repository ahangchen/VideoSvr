package cwh.web.servlet;

import cwh.web.session.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cwh on 16-3-3
 */
@WebServlet(name = "Touch")
public class Touch extends HttpServlet {
    public static String TAG = "Touch";
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sid = request.getParameter("sid");
        if(SessionManager.getInstance().touch(sid)){
            ServletHelper.responseString(response, ServletHelper.genErrCode(0, "Delay session clean for session: " + sid));
        } else {
            ServletHelper.responseString(response, ServletHelper.genErrCode(5, "Touch Session: " + sid + "failed"));
        }
    }
}

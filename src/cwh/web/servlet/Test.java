package cwh.web.servlet;

import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.servlet.playback.PlaybackHelper;
import cwh.web.session.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cwh on 16-1-7
 */
@WebServlet(name = "Test")
public class Test extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String sid = request.getParameter(CommonDefine.SID);
//        Object obj;
//        if ((obj = getServletContext().getAttribute(sid)) == null) {
//            VSLog.d("obj null");
//            obj = sid + "obj";
//            getServletContext().setAttribute(sid, obj);
//        }
//        VSLog.d(obj.toString());
        PlaybackHelper.responseString(response, request.getSession().toString());
        VSLog.d(request.getSession().toString());
        VSLog.d(request.getServletContext().toString());
        VSLog.d(SessionManager.getInstance().toString());
    }
}

package cwh.web.servlet;

import cwh.utils.StringUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
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
@WebServlet(name = "SessionClean")
public class SessionClean extends HttpServlet {
    // http://localhost:8888/VideoSvr/SessionClean?sid=12121212
    // return 木有
    public static String TAG = "SessionClean";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        VSLog.d(TAG, "Session clean param:" + request.getQueryString());
        String sid = request.getParameter(CommonDefine.SID);
        if (sid == null) {
            VSLog.e(TAG, "clean but not sid");
            VSLog.d(TAG, "try get sid from request");
            sid = request.getSession().getId();
        }

        if (!StringUtils.isMatch(sid, ServletHelper.REGX_SID)) {
            VSLog.e(TAG, "illegal sid:" + sid);
            ServletHelper.responseString(response, ServletHelper.genErrCode(1, "illegal sid:" + sid));
            return;
        }
        ServletHelper.responseString(response, ServletHelper.genErrCode(0, "quick close"));
        if (SessionManager.getInstance().sessionClean(sid)) {
            VSLog.d(TAG, ServletHelper.genErrCode(0, "Session " + sid + "cleaned"));
        } else {
            VSLog.d(TAG, ServletHelper.genErrCode(4, "no such session:" + sid));
        }
    }
}

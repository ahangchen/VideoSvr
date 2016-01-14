package cwh.web.servlet.realplay;

import cwh.utils.log.VSLog;
import cwh.web.model.QueryVideoListerner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cwh on 16-1-2
 */
@WebServlet(name = "RealPlay", asyncSupported = true)
public class RealPlay extends HttpServlet {
    // http://localhost:8888/VideoSvr/RealPlay?ip=192.168.199.108&port=554&channel=1&sid=12121212
    // return {"sid":"123423411","rpp":"/home/cwh"}
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        getServletContext().setAttribute("");
        // todo 如果参数格式不正常，拒绝请求
        VSLog.d("param:" + request.getQueryString());
        RealPlayHelper.asyncResponse(request, new QueryVideoListerner());
    }
}

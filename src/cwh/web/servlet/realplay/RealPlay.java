package cwh.web.servlet.realplay;

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
@WebServlet(name = "RealPlay")
public class RealPlay extends HttpServlet {
    // http://localhost:8888/VideoSvr/RealPlay?ip=192.168.199.108&channel=1
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        getServletContext().setAttribute("");
        RealPlayHelper.asyncResponse(request, new QueryVideoListerner());
    }
}

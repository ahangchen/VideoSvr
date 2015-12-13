package cwh.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cwh on 15-12-13.
 */
@WebServlet(name = "Time2Video")
public class Time2Video extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String startTime = request.getParameter("start");
        String endTime = request.getParameter("end");
        PrintWriter out =response.getWriter();
        out.println("/videocache/"+startTime+"_"+endTime+"_video.mp4");
        out.close();
    }
}

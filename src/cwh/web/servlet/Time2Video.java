package cwh.web.servlet;

import cwh.web.model.AsyncQueryVideo;
import cwh.web.model.QueryVideoListerner;
import cwh.web.utils.StringUtils;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cwh on 15-12-13
 * 这个类主要做调度用，包括request的get，response的set，其他model的调用
 */
@WebServlet(name = "Time2Video", asyncSupported = true)
public class Time2Video extends HttpServlet{

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        ServletHelper.asyncResponse(request, new QueryVideoListerner());
    }
}

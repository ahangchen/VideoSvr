package cwh.web.servlet.playback;

import cwh.utils.log.VSLog;
import cwh.web.model.playback.PlaybackState;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by cwh on 16-1-7
 */
@WebServlet(name = "PlaybackClean")
public class PlaybackClean extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sid = request.getParameter("sid");
        if (sid == null) return; //不清理，等timeout来做
        Object playbackState;
        if ((playbackState = getServletContext().getAttribute(sid)) == null) {
            VSLog.d("obj null");
            //发起清理时sid不对，也用timeout来清理
        }

    }
}

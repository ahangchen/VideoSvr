package cwh.web.servlet.file;

import cwh.utils.file.FileUtils;
import cwh.web.model.CommonDefine;
import cwh.web.servlet.ServletHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by cwh on 16-1-13
 */
@WebServlet(name = "Download")
public class Download extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * http://localhost:8888/Download?realplay/125-216-231-164-554-1/t.m3u8
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filePath = CommonDefine.DATA_PATH + "/" + request.getQueryString();
        if(filePath.contains("..")) {
            ServletHelper.responseString(response, ServletHelper.genErrCode(2, "Illegal path"));
        } else {
            if (FileUtils.isExist(filePath)) {
                FileInputStream fis = new FileInputStream(filePath);
                byte []tmp = new byte[10000];
                while (fis.read(tmp) != -1) {
                    response.getOutputStream().write(tmp);
                    response.getOutputStream().flush();
                }
            } else {
                ServletHelper.responseString(response, ServletHelper.genErrCode(1, "file not found"));
            }
        }
    }
}

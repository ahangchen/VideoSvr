package cwh.web.listener; /**
 * Created by cwh on 16-1-7
 */

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;
import cwh.web.session.SessionManager;
import cwh.web.session.SessionState;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener()
public class CleanListener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener, ServletRequestListener {

    public static String TAG = "CleanListener";

    // Public constructor is required by servlet spec
    public CleanListener() {
        VSLog.d(TAG, "create");
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */
        VSLog.d(TAG, "context initial");
    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
        ThreadUtils.shutdown();
        VSLog.d(TAG, "context destroy");
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
        VSLog.d(TAG, "session create");
    }

    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
        VSLog.d(TAG, "session destroy");
        String sid = se.getSession().getId();// 超时时用session本身的id，主动清理用参数里的id
        SessionState sessionState = SessionManager.getInstance().getSessionState(sid);
        if (sessionState == null) {
            VSLog.d(TAG, "no such session:" + sid);
        }
        SessionManager.getInstance().sessionClean(sessionState);
        VSLog.d(TAG, "session" + sid + " destroyed");
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
//        VSLog.d(TAG, "attr add");
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
//        VSLog.d(TAG, "attr remove");
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
//        VSLog.d(TAG, "attr replace");
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
//        VSLog.d(TAG, "request destroy");
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
//        VSLog.d(TAG, "request init");
    }
}

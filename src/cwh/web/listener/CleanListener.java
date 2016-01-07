package cwh.web.listener; /**
 * Created by cwh on 16-1-7
 */

import cwh.utils.log.VSLog;

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

    // Public constructor is required by servlet spec
    public CleanListener() {
        VSLog.d("create");
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */
        VSLog.d("context initial");
    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
        VSLog.d("context destroy");
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
        VSLog.d("session create");
    }

    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
        VSLog.d("session destroy");
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
        VSLog.d("attr add");
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
        VSLog.d("attr remove");
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
        VSLog.d("attr replace");
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        VSLog.d("request destroy");
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        VSLog.d("request init");
    }
}

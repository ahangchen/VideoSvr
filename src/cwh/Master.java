package cwh;

import cwh.NVR.Log.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by cwh on 15-11-28
 * 编译后将out/artifacts/videosvr_war_exploded 打成zip,rename为war；
 *
 * 首先，直接把相应的war包放到$TOMCAT_HOME/webapps下，不用建目录；
 * 然后，修改$TOMCAT_HOME/conf/server.xml，在Host配置段中添加类似于如下内容：
 * <Context path="/" docBase="VideoSvr.war" debug="0" privileged="true" reloadable="true"/>
 * 其中，docBase参数标识的是war包的名称。
 * 访问时，使用如下地址进行访问：http://ip:port/VideoSvr

 * 如果要把war包部署到Tomcat根目录，直接使用http://ip:port进行访问，需要的操作稍微复杂一些：
 * 一、把war包解压，部署到除$TOMCAT_HOME/webapps以外的目录
 * 二、删除$TOMCAT_HOME/ webapps/ROOT目录下的所有文件
 * 三、在$TOMCAT_HOME/conf/Catalina/localhost目录下，新建一个ROOT.xml文件，写入类似于如下内容：
 * <?xml version='1.0' encoding='utf-8'?>
    <Context path="/" docBase="/usr/local/tomcat-6.0/webdav" debug="0" privileged="true" reloadable="true"/>
 其中，docBase指向的是war包解压后的目录名称，需绝对路径。
 */
public class Master {
    public static void cp(File f1,File f2){
        try {
            int length = 1024;
            FileInputStream in = new FileInputStream(f1);
            FileOutputStream out = new FileOutputStream(f2);
            byte[] buffer = new byte[length];
            while (true) {
                int ins = in.read(buffer);
                if (ins == -1) {
                    in.close();
                    out.flush();
                    out.close();
                } else
                    out.write(buffer, 0, ins);
                Thread.sleep(1000);
                System.out.println("cp");
            }
        } catch (Exception e){

        }
    }
    public static void main(String[] args) {
        LogManager.getInstance().displayLogs();
//        new File("/home/cwh/Mission/lab/data/videoweb/video.mp4").delete();
//        try {
//            cp(new File("/home/cwh/Mission/lab/data/videoweb/video0.mp4"),new File("/home/cwh/Mission/lab/data/videoweb/video.mp4"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("copy finish");
    }
}

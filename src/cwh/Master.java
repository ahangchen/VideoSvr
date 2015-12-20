package cwh;

import cwh.NVR.Log.LogManager;
import cwh.NVR.NVRNative;
import cwh.NVR.play.PlayCallback;
import cwh.NVR.play.PlayCallbackImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by cwh on 15-11-28
 * 第一次在tomcat的首页中，用manager的deploy部署，
 * 之后run，编class
 * cp /home/cwh/coding/Mission/VideoSvr/out/artifacts/VideoSvr_war_exploded/. -r /home/cwh/tomcat/webapps/VideoSvr
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
//        LogManager.getInstance().displayLogs();
        NVRNative.time2VideoPath(0, 2015, 12, 11, 0, 0, 0, 2015, 12, 11, 0, 0, 3, new PlayCallback(){
            @Override
            public void onComplete(String filePath) {
                System.out.println("Java onComplete :" + filePath);
            }

        });
//        new File("/home/cwh/Mission/lab/data/videoweb/video.mp4").delete();
//        try {
//            cp(new File("/home/cwh/Mission/lab/data/videoweb/video0.mp4"),new File("/home/cwh/Mission/lab/data/videoweb/video.mp4"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("copy finish");
    }
}

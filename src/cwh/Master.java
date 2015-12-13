package cwh;

import cwh.NVR.Log.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by cwh on 15-11-28
 */
public class Master {
    public static void cp(File f1,File f2){
        try {
            long time = new Date().getTime();
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

package cwh;

import cwh.web.model.realplay.AsyncRealPlay;

/**
 * Created by cwh on 15-11-28
 * 第一次在tomcat的首页中，用manager的deploy部署，
 * 之后run， build artifact rebuild
 * 测试环境
 * cp /home/cwh/Mission/coding/VideoSvr/out/artifacts/VideoSvr_war_exploded/. -r /home/cwh/tomcat/webapps/VideoSvr
 * 正式环境
 *  rmdir -rf powerful & mkdir powerful & cp lib/libnvr.so powerful/libnvr.so & cp out/artifacts/VideoSvr_war_exploded/. -r powerful/VideoSvr & tar cvf powerful.tar.gz powerful & scp -r powerful.tar.gz cwh@222.201.145.237:/home/cwh & rm -rf powerful
 */
public class Master {

    public static void main(String[] args) {
//        LogManager.getInstance().displayLogs();

//        NVRNative.time2VideoPath(0, 2015, 12, 11, 0, 0, 0, 2015, 12, 11, 0, 0, 3, new PlayCallback(){
//            @Override
//            public void onComplete(String filePath) {
//                VSLog.d(TAG, "Java onComplete :" + filePath);
//            }
//
//        });
        final Process convert = AsyncRealPlay.sysRealPlay("192.168.199.108", "554", "1");
        new Thread() {
            @Override
            public void run() {
                try {
                    convert.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
//        VSLog.d(TAG, "1");
//        M3U8Mng.dueClean(M3U8Mng.realPlayDir("125.216.247.121", "38888", "1"), 3);
    }
}

/**
 * 0. ts删除放宽松 OK
 * 1. servlet实现文件服务器，放到同个进程(就是提供一个下载的servlet)
 * 2. 目录名以摄像头为准 OK
 * 2.5 直播资源清理，实现最后一个直播断开监听
 * 3. 返回时间校准（校正电脑时间，realplay返回当前时间给客户端） OK
 * 4. 清理线程泄露 OK（不开线程）
 */
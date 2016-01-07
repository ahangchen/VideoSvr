package cwh.web.model.realplay;

import cwh.utils.file.FileUtils;
import cwh.utils.log.VSLog;

import java.io.*;

/**
 * Created by cwh on 16-1-6
 */
public class M3U8Mng {
    private static boolean stopRealPlay = false;

    public static int curTSNum() {
        String curM3U8Name = AsyncRealPlay.realPlayPath("192.168.199.108", "554", "1");

        File file = new File(curM3U8Name);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //一次读一行，读入null时文件结束
            while ((tempString = reader.readLine()) != null) {
                if (!tempString.substring(0, 1).equals("#")) {
                    VSLog.log(VSLog.DEBUG, tempString + " " + Integer.parseInt(tempString.substring(1).split("\\.")[0]));
                    return Integer.parseInt(tempString.substring(1).split("\\.")[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassCastException e){
          e.printStackTrace();
            //也可能M3U8文件里还没ts记录，发生空转int错误，返回-1
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        //读不到非#开头的行，-1代表全部保留
        return -1;
    }

    public static void timelyClean(String curPath) {
        while (!stopRealPlay) {
            dueClean(curPath, curTSNum());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void dueClean(String curPath, final int curRcd) {
        FileUtils.flatTravel(curPath, new FileUtils.Travel() {
            @Override
            public void onFile(File file) {
                int tsNum = getTsNum(file);
                if (curRcd == -1 || tsNum == -1) return;
                if (curRcd > tsNum) {
                    VSLog.log(VSLog.DEBUG, "to delete");
                    file.delete();
                }
            }
        });
    }

    public static int getTsNum(File file) {
        VSLog.log(VSLog.DEBUG, file.getName());
        if (file.getName().equals("t.m3u8")) return -1;
        return Integer.parseInt(file.getName().substring(1).split("\\.")[0]);
    }

    public static void main(String[]args) {
        timelyClean(AsyncRealPlay.realPlayDir("192.168.199.108", "554", "1"));
    }
}

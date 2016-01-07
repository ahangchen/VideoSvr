package cwh.utils.file;

import cwh.utils.log.VSLog;

import java.io.*;

/**
 * Created by cwh on 16-1-5
 */
public class FileUtils {
    public interface Travel {
        public void onFile(File file);
    }

    // 非递归，只遍历第一层
    public static void flatTravel(String dirStr, Travel onTravel) {
        File dir = new File(dirStr);
        File[] listFiles = dir.listFiles();
        if (listFiles == null) {
            VSLog.log(VSLog.DEBUG, "listFiles null");
            return;
        }
        for (File f : listFiles) {
            if (f.isFile()) {
                VSLog.log(VSLog.DEBUG, "isFile");
                onTravel.onFile(f);
            }
        }
    }

    public interface ReadLine{
        void onLine(String string);
    }
    public static void readLine(String fileName,ReadLine readLine){
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //一次读一行，读入null时文件结束
            while ((tempString = reader.readLine()) != null) {
                //把当前行号显示出来
                readLine.onLine(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static boolean rm(String dirPath) {
        File dir = new File(dirPath);
        return dir.delete();
    }

    public static void cp(File f1, File f2) {
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
                    break;
                } else
                    out.write(buffer, 0, ins);
                Thread.sleep(1000);
                VSLog.log(VSLog.DEBUG, "cp");
                VSLog.log(VSLog.DEBUG, "cp");
            }
        } catch (Exception e) {
            VSLog.err("cp", e);
        }
    }
}

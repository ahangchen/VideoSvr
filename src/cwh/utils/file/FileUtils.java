package cwh.utils.file;

import cwh.utils.log.VSLog;

import java.io.*;

/**
 * Created by cwh on 16-1-5
 */
public class FileUtils {
    public static String TAG = "FileUtils";
    public interface Travel {
        public void onFile(File file);
    }

    // 非递归，只遍历第一层
    public static void flatTravel(String dirStr, Travel onTravel) {
        File dir = new File(dirStr);
        File[] listFiles = dir.listFiles();
        if (listFiles == null) {
            VSLog.d(TAG, "listFiles null");
            return;
        }
        for (File f : listFiles) {
            if (f.isFile()) {
//                VSLog.d(TAG, "isFile");
                onTravel.onFile(f);
            }
        }
    }

    public interface ReadLine {
        void onLine(String string);
    }

    public static void readLine(String fileName, ReadLine readLine) {
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
                VSLog.d(TAG, "cp");
                VSLog.d(TAG, "cp");
            }
        } catch (Exception e) {
            VSLog.err(TAG, "cp", e);
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    VSLog.d(TAG, "delete filedir failed:" + child);
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static boolean rmDir(String dirPath) {
        return deleteDir(new File(dirPath));
    }

    public static boolean isExist(String filePath) {
        return new File(filePath).exists();
    }

    public static void main(String[] args) {
//        FileUtils.flatTravel("/media/Software/lab/data/videoweb/realplay/192-168-199-108-554-1-2016-0-9", new Travel() {
//            @Override
//            public void onFile(File file) {
//                VSLog.d(file.getName());
//            }
//        });
        rmDir("/media/Software/lab/data/videoweb/realplay/192-168-199-108-554-1-2016-0-9");
    }
}

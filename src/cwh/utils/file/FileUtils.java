package cwh.utils.file;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.log.VSLog;
import cwh.web.model.CommonDefine;

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
//            VSLog.d(TAG, "listFiles null");
            return;
        }
        for (File f : listFiles) {
//                VSLog.d(TAG, "isFile");
            onTravel.onFile(f);
        }
    }

    public interface ReadLine {
        void onLine(String string, int lineIndex);
    }

    public static void readLine(String fileName, ReadLine readLine) {
        File file = new File(fileName);
        BufferedReader reader = null;
        int lineIndex = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //一次读一行，读入null时文件结束

            while ((tempString = reader.readLine()) != null) {
                //把当前行号显示出来
                readLine.onLine(tempString, lineIndex);
                lineIndex++;
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

    public static int lineCount(String filePath) {
        final int[] lineCount = new int[1];
        lineCount[0] = 0;
        readLine(filePath, new ReadLine() {
            @Override
            public void onLine(String string, int lineIndex) {
                lineCount[0] = lineIndex;
            }
        });
        return lineCount[0];
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void append2File(String fileName, String content) {
        if (!new File(fileName).exists()) {
            createFile(fileName);
        }
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void overWriteFile(String fileName, String content) {
        if (!new File(fileName).exists()) {
            createFile(fileName);
        }
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName, false);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                System.out.println("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                System.out.println("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
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
        if (filePath == null) return false;
        return new File(filePath).exists();
    }

    public static boolean mkdir(String path) {
        return new File(path).mkdir();
    }

    public static boolean rename(String src, String dst) {
        return new File(src).renameTo(new File(dst));
    }


    public static void main(String[] args) {
//        FileUtils.flatTravel("/media/Software/lab/data/videoweb/realplay/192-168-199-108-554-1-2016-0-9", new Travel() {
//            @Override
//            public void onFile(File file) {
//                VSLog.d(file.getName());
//            }
//        });
//        rmDir("/media/Software/lab/data/videoweb/realplay/192-168-199-108-554-1-2016-0-9");
//        FileUtils.rmDir(CommonDefine.DATA_PATH + "/" + CommonDefine.REAL_PLAY_DIR_PATH);
//        FileUtils.rmDir(CommonDefine.PLAY_BACK_DIR_PATH);
//        FileUtils.mkdir(CommonDefine.DATA_PATH + "/" + CommonDefine.REAL_PLAY_DIR_PATH);
//        FileUtils.mkdir(CommonDefine.PLAY_BACK_DIR_PATH);
//        writing();
//        FileUtils.overWriteFile("test.txt", "nothing\n" +
//                " \n" +
//                " \n" +
//                " \n");
        VSLog.d(TAG, lineCount("/home/cwh/Mission/coding/VideoSvr/src/cwh/Master.java")+"");
    }


}

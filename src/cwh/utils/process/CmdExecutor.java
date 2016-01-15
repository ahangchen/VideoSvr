package cwh.utils.process;

import cwh.utils.concurrent.ThreadUtils;
import cwh.utils.log.VSLog;

import java.io.*;

/**
 * Created by cwh on 16-1-2
 */
public class CmdExecutor {
    public static String TAG = "CmdExecutor";

    public static String exec(String cmd) {
        try {
            String[] cmdA = {"/bin/sh", "-c", cmd};
            Process process = Runtime.getRuntime().exec(cmdA);
            LineNumberReader br = new LineNumberReader(
                    new InputStreamReader(process
                            .getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                VSLog.d(TAG, line);
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void wait(String command) {
        Process proc = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            proc = runtime.exec(command);
            InputStream err = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(err);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

//            VSLog.d(TAG, proc.toString());
            while ((line = br.readLine()) != null) {
                VSLog.d(TAG, line);
            }
            int exitVal = proc.waitFor();
            VSLog.d(TAG, "Process exitValue: " + exitVal);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static Process run(String command) {
        Process proc = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            VSLog.d(TAG, command);
            proc = runtime.exec(command);

//          todo  打调用过程中的输出,有些log很多，后期还是给它单独开一个log文件比较好
            InputStream err = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(err);
            final BufferedReader br = new BufferedReader(isr);
            // 开一个新线程，否则会卡进程
            ThreadUtils.runInBackGround(new Runnable() {
                @Override
                public void run() {
                    String line = null;
                    try {
                        while ((line = br.readLine()) != null) {
                            if (line.startsWith("frame=")) {
                                continue;
                            }
                            VSLog.d(TAG, line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 这里的stream由外层proc控制关闭
                }
            });

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return proc;
    }

    public static void main(String[] args) {
        String rst = exec("ls");
        VSLog.d(TAG, rst);
    }
}

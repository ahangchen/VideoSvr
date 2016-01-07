package cwh.utils.process;

import cwh.utils.log.VSLog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by cwh on 16-1-2
 */
public class CmdExecutor {
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
                VSLog.log(VSLog.DEBUG, line);
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

//            VSLog.log(VSLog.DEBUG, proc.toString());
            while ((line = br.readLine()) != null) {
                VSLog.log(VSLog.DEBUG, line);
            }
            int exitVal = proc.waitFor();
            VSLog.log(VSLog.DEBUG, "Process exitValue: " + exitVal);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static Process run(String command) {
        Process proc = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            proc = runtime.exec(command);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return proc;
    }

    public static void main(String[] args) {
        String rst = exec("ls");
        VSLog.log(VSLog.DEBUG, rst);
    }
}

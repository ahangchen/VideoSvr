package cwh.utils;

import cwh.utils.log.VSLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cwh on 16-1-15
 */
public class StringUtils {
    public static String TAG = "StringUtils";
    public static String REGX_IP = "^(\\d{3}|\\d{2}|\\d{1})\\.(\\d{3}|\\d{2}|\\d{1})\\.(\\d{3}|\\d{2}|\\d{1})\\.(\\d{3}|\\d{2}|\\d{1})$";
    public static String REGX_POS_INT = "[0-9]*[1-9][0-9]*";

    public static boolean isMatch(String dstStr, String strReg) {
        Pattern pattern = Pattern.compile(strReg);
        Matcher matcher = pattern.matcher(dstStr);
        return matcher.find();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static int[] str2Ips(String sIp) {
        int[] iIps = new int[4];
        String[] ips = sIp.split("\\.");
        iIps[0] = Integer.parseInt(ips[0]);
        iIps[1] = Integer.parseInt(ips[1]);
        iIps[2] = Integer.parseInt(ips[2]);
        iIps[3] = Integer.parseInt(ips[3]);
        return iIps;
    }

    public static void main(String[]args) {
        VSLog.d(TAG, isMatch("125.216.2.4", REGX_IP)? "true" : "false");
    }
}

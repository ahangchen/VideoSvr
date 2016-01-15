package cwh.utils;

import cwh.utils.log.VSLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cwh on 16-1-15
 */
public class StringUtils {
    public static String TAG = "StringUtils";
    public static String REGX_IP = "(\\d{3}|\\d{2}|\\d{1}).(\\d{3}|\\d{2}|\\d{1}).(\\d{3}|\\d{2}|\\d{1}).(\\d{3}|\\d{2}|\\d{1})";
    public static String REGX_POS_INT = "[0-9]*[1-9][0-9]*";

    public static boolean isMatch(String dstStr, String strReg) {
        Pattern pattern = Pattern.compile(strReg);
        Matcher matcher = pattern.matcher(dstStr);
        boolean find = matcher.find();
        while (matcher.find()) {
            System.out.println("start(): " + matcher.start());
            System.out.println("end(): " + matcher.end());
        }
        return find;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static void main(String[]args) {
        VSLog.d(isMatch("125.216.2.122", REGX_IP)+"");
    }
}

package cwh.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created by cwh on 16-1-7
 */
public class VMath {
    /*
	 * @desc 64位整数的映射
	 */
    public static long hashLong(String value) {
        long seed = 131; // 31 131 1313 13131 131313 etc..  BKDRHash
        long hash=0;
        for (int i = 0; i< value.length(); i++) {
            hash = (hash * seed) + value.charAt(i);
        }
        return hash;
    }
    public static String hashLong(HttpServletRequest request) {
        //用session的内存地址和日期来唯一标识一个会话
        String value = request.getSession().toString() + new Date().getTime();
        long seed = 131; // 31 131 1313 13131 131313 etc..  BKDRHash
        long hash=0;
        for (int i = 0; i< value.length(); i++) {
            hash = (hash * seed) + value.charAt(i);
        }
        return String.valueOf(hash);
    }
}

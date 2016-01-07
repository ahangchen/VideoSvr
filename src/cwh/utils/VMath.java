package cwh.utils;

/**
 * Created by cwh on 16-1-7.
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

}

package cwh.utils.concurrent;

import cwh.utils.log.VSLog;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cwh on 16-1-16
 */
public class LockTest {
    public static String TAG = "LockTest";
    public static void main(String[]args) {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            VSLog.d(TAG, "0");
            ThreadUtils.sleep(1000);
            VSLog.d(TAG, "1");
//            lock.unlock();
        } catch (Throwable e) {
            VSLog.e(TAG, "lock", e);
        } finally{
            lock.unlock();
        }
    }
}

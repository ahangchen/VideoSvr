package cwh.utils.concurrent;

import cwh.utils.log.VSLog;

import java.util.concurrent.*;

/**
 * Created by cwh on 15-12-25
 */
public class ThreadUtils {
    public static String TAG = "ThreadUtils";
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 200, 50000L,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(100),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread s = Executors.defaultThreadFactory().newThread(r);
                    // 防止tomcat中线程泄露
                    s.setDaemon(true);
                    return s;
                }
            });

    public static void runInBackGround(Runnable task) {
        executor.execute(task);
    }

    public static void runInBackGround(final Task task) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                task.callback.beforeRun();
                boolean result = task.run();
                task.callback.onComplete();
                if (result) {
                    task.callback.onSuccess();
                } else {
                    task.callback.onError();
                }
            }
        });
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        executor.shutdownNow();
    }
}

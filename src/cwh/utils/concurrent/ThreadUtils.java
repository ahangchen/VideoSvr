package cwh.utils.concurrent;

import cwh.utils.log.VSLog;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by cwh on 15-12-25
 */
public class ThreadUtils {
	private static ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

	public static void runInBackGround(Runnable task) {
		executor.execute(task);
	}

	public static void runInBackGround(final Task task){
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
			VSLog.e("sleep interrupted", e);
		}
	}
}

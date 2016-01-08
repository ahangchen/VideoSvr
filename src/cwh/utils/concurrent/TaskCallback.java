package cwh.utils.concurrent;

/**
 * Created by cwh on 15-12-25.
 */
public class TaskCallback {
	private Runnable empty = new Runnable() {
		@Override
		public void run() {

		}
	};

	private Runnable beforeRun = empty;
	private Runnable run = empty;
	private Runnable onComplete = empty;
	private Runnable onSuccess = empty;
	private Runnable onError = empty;

	public void setBeforeRun(Runnable beforeRun) {
		this.beforeRun = beforeRun;
	}

	public void setOnComplete(Runnable onComplete) {
		this.onComplete = onComplete;
	}

	public void setOnSuccess(Runnable onSuccess) {
		this.onSuccess = onSuccess;
	}

	public void setOnError(Runnable onError) {
		this.onError = onError;
	}

	public void beforeRun() {
		beforeRun.run();
	}

	public void onComplete() {
		onComplete.run();
	}
	public void onSuccess() {
		onSuccess.run();
	}

	public void onError() {
		onError.run();
	}
}

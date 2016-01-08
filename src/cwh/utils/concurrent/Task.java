package cwh.utils.concurrent;

/**
 * Created by cwh on 15-12-25.
 */
public abstract class Task {
	public TaskCallback callback;
	public void setCallback(TaskCallback callback) {
		this.callback = callback;
	}
	public abstract boolean run();
}

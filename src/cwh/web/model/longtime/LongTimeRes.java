package cwh.web.model.longtime;

/**
 * Created by cwh on 16-3-10
 */
public class LongTimeRes {
    public LongTimeRes(String longTimeDir) {
        this.longTimeDir = longTimeDir;
    }

    private String longTimeDir;

    public String getLongTimeDir() {
        return longTimeDir;
    }

    public void setLongTimeDir(String longTimeDir) {
        this.longTimeDir = longTimeDir;
    }

    public boolean stop = false;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public String toJson(String sid) {
        return "{" +
                "\"err\":\"0\"," +
                "\"sid\":\"" + sid +
                "\"," +
                "\"rpp\":\"" + getLongTimeDir() +
                "\"}";
    }
}

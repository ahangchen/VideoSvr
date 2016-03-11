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

    public String toJson(String sid) {
        return "{" +
                "\"sid\":\"" + sid +
                "\"," +
                "\"rpp\":\"" + getLongTimeDir() +
                "\"}";
    }
}

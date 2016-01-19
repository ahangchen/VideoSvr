package cwh.web.model;

/**
 * Created by cwh on 16-1-19
 */
public class NvrInfo {
    private String ip;
    private String port;

    public NvrInfo(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

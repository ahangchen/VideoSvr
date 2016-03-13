package cwh.web.model.longtime;

import cwh.web.model.CommonDefine;

import java.io.File;

/**
 * Created by cwh on 16-3-13
 */
public class LongTimeParam {
    public static String TAG = "PlayBackParam";

    int[] ip = new int[4];
    int port;

    int channel;

    int startYear;
    int startMon;
    int startDay;
    int startHour;
    int startMin;
    int startSec;

    public LongTimeParam(String ip, String port, String channel, String startDateTime) {
        int[] tip = new int[4];
        String[] ips = ip.split("\\.");
        tip[0] = Integer.parseInt(ips[0]);
        tip[1] = Integer.parseInt(ips[1]);
        tip[2] = Integer.parseInt(ips[2]);
        tip[3] = Integer.parseInt(ips[3]);
        setIp(tip);
        setPort(Integer.parseInt(port));
        setChannel(Integer.parseInt(channel));
        String[] startParam = startDateTime.split("-");
        setStartYear(Integer.parseInt(startParam[0]));
        setStartMon(Integer.parseInt(startParam[1]));
        setStartDay(Integer.parseInt(startParam[2]));
        setStartHour(Integer.parseInt(startParam[3]));
        setStartMin(Integer.parseInt(startParam[4]));
        setStartSec(Integer.parseInt(startParam[5]));
    }

    public int[] getIp() {
        return ip;
    }

    public void setIp(int[] ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartMon() {
        return startMon;
    }

    public void setStartMon(int startMon) {
        this.startMon = startMon;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getStartSec() {
        return startSec;
    }

    public void setStartSec(int startSec) {
        this.startSec = startSec;
    }


    @Override
    public String toString() {
        return getIp()[0] + "-" + getIp()[1] + "-" + getIp()[2] + "-" + getIp()[3] + "-" +
                getPort() + "-" + getChannel() + "-" +
                getStartYear() + "-" + getStartMon() + "-" + getStartDay() + "-" +
                getStartHour() + "-" + getStartMin() + "-" + getStartSec();
    }

    public static String formatPath(LongTimeParam param) {
        return CommonDefine.PLAY_BACK_DIR_PATH + File.separator + param.toString() + CommonDefine.LONG_TIME;
    }

}

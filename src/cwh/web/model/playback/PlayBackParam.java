package cwh.web.model.playback;

import cwh.web.model.CommonDefine;

/**
 * Created by cwh on 15-12-13
 */
public class PlayBackParam {
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

    int endYear;
    int endMon;
    int endDay;
    int endHour;
    int endMin;
    int endSec;

    public PlayBackParam(String ip, String port, String channel, String startDateTime, String endDateTime) {
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

        String[] endParam = endDateTime.split("-");
        setEndYear(Integer.parseInt(endParam[0]));
        setEndMon(Integer.parseInt(endParam[1]));
        setEndDay(Integer.parseInt(endParam[2]));
        setEndHour(Integer.parseInt(endParam[3]));
        setEndMin(Integer.parseInt(endParam[4]));
        setEndSec(Integer.parseInt(endParam[5]));

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

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getEndMon() {
        return endMon;
    }

    public void setEndMon(int endMon) {
        this.endMon = endMon;
    }

    public int getEndDay() {
        return endDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public int getEndSec() {
        return endSec;
    }

    public void setEndSec(int endSec) {
        this.endSec = endSec;
    }

    @Override
    public String toString() {
        return getIp()[0] + "-" + getIp()[1] + "-" + getIp()[2] + "-" + getIp()[3] + "-" +
                getPort() + "-" + getChannel() + "-" +
                getStartYear() + "-" + getStartMon() + "-" + getStartDay() + "-" +
                getStartHour() + "-" + getStartMin() + "-" + getStartSec() + "-" +
                getEndYear() + "-" + getEndMon() + "-" + getEndDay() + "-" +
                getEndHour() + "-" + getEndMin() + "-" + getEndSec()
                ;
    }

    public static String formatPath(PlayBackParam param) {
        return param.toString() + CommonDefine.MP4;
    }

    public static void main(String[]args) {
    }

}

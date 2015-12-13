package cwh.web.utils;

import cwh.web.model.VideoQueryParam;

/**
 * Created by cwh on 15-12-13
 */
public class StringUtils {
    public static String formatVideoPath(String startTime, String endTime) {
        String filePath;
        if (startTime == null || endTime == null) {
            filePath = "video.mp4";
        } else {
            filePath = "/videocache/" + startTime + "_" + endTime + "_video.mp4";
        }
        return filePath;
    }

    public static VideoQueryParam DateTime2Param(String channel, String startDateTime, String endDateTime){
        VideoQueryParam vqp = new VideoQueryParam();
        vqp.setChannel(Integer.parseInt(channel));
        String[] startParam = startDateTime.split("-");
        vqp.setStartYear(Integer.parseInt(startParam[0]));
        vqp.setStartMon(Integer.parseInt(startParam[1]));
        vqp.setStartDay(Integer.parseInt(startParam[2]));
        vqp.setStartHour(Integer.parseInt(startParam[3]));
        vqp.setStartMin(Integer.parseInt(startParam[4]));
        vqp.setStartSec(Integer.parseInt(startParam[5]));

        String[] endParam = endDateTime.split("-");
        vqp.setEndYear(Integer.parseInt(endParam[0]));
        vqp.setEndMon(Integer.parseInt(endParam[1]));
        vqp.setEndDay(Integer.parseInt(endParam[2]));
        vqp.setEndHour(Integer.parseInt(endParam[3]));
        vqp.setEndMin(Integer.parseInt(endParam[4]));
        vqp.setEndSec(Integer.parseInt(endParam[5]));

        return vqp;
    }
}

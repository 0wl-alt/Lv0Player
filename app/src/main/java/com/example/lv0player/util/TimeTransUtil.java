package com.example.lv0player.util;

public class TimeTransUtil {
    public static String millsToTime(int mills){
        int seconds = (mills/1000)%60;
        int minutes = (mills/1000/60)%60;
        String time = new String();
        time+=String.format("%02d",minutes)+":"+String.format("%02d",seconds);
        return time;
    }
}

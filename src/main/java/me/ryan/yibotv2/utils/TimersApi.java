package me.ryan.yibotv2.utils;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class TimersApi {

    public static String convertMillis(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis),
                hours = TimeUnit.MILLISECONDS.toHours(millis) % 24,
                minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        StringBuilder text = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#00");
        if (days > 0) text.append(df.format(days)).append(":");
        if (hours > 0) text.append(df.format(hours)).append(":");
        if (minutes > 0) text.append(df.format(minutes)).append(":");
        else text.append("00:");
        if (seconds > 0) text.append(df.format(seconds));
        return text.toString();
    }

}

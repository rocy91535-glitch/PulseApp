package com.pulse.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String format(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        if (diff < 60000) return "adesso";
        if (diff < 3600000) return (diff / 60000) + " min fa";
        if (diff < 86400000) return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
        return new SimpleDateFormat("dd/MM", Locale.getDefault()).format(new Date(timestamp));
    }
}

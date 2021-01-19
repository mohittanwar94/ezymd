package com.ezymd.restaurantapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String getReadableDate(String duedate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(duedate);
            duedate = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH).format(date);
            return duedate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isOrderLive(String duedate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(duedate);
            assert date != null;
            SnapLog.print(""+(System.currentTimeMillis() - date.getTime() <= 120000L));
            return System.currentTimeMillis() - date.getTime() <= 120000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static long getDuration(String duedate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(duedate);
            assert date != null;
            SnapLog.print(""+(System.currentTimeMillis() - date.getTime() <= 60000L));
            return System.currentTimeMillis() - date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0L;
    }
}

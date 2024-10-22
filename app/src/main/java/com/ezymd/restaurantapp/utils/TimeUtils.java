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

    public static boolean isOrderLive(String create) {
        try {
            Date created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(create);
            assert created != null;
            SnapLog.print("" + (created.getTime() - System.currentTimeMillis() <= 60000L));
            return (created.getTime() - System.currentTimeMillis()) > 0L;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static long getDuration(String created) {
        try {
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(created);
            assert createdDate != null;
            SnapLog.print("" + (createdDate.getTime() - System.currentTimeMillis() <= 60000L));
            return Math.abs(createdDate.getTime() - System.currentTimeMillis());
               /* return 60000L;
            else
                return 0L;*/


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0L;
    }
}

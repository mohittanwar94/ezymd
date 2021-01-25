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

    public static boolean isOrderLive(String create, String updated) {
        try {
            Date created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(create);
            Date updatedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(updated);
            assert created != null;
            SnapLog.print("" + (updatedDate.getTime() - created.getTime() <= 60000L));
            return (updatedDate.getTime() - created.getTime() < 60000L);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static long getDuration(String updated, String created) {
        try {
            Date updatedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(updated);
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(created);
            assert createdDate != null;
            SnapLog.print("" + (updatedDate.getTime() - createdDate.getTime() <= 60000L));

            if ((updatedDate.getTime() - createdDate.getTime() < 10000L) || (updatedDate.getTime() - createdDate.getTime() == 0L)) {
                return 60000L;
            } else if ((updatedDate.getTime() - createdDate.getTime() > 60000L)) {
                return 0L;
            } else {
                return (updatedDate.getTime() - createdDate.getTime());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0L;
    }
}

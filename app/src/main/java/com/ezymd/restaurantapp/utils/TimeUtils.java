package com.ezymd.restaurantapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String getReadableDate(String duedate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).parse(duedate);
            duedate = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH).format(date);
            return duedate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}

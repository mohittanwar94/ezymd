package com.ezymd.restaurantapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TopExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Activity app = null;

    public TopExceptionHandler(Activity app) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.app = app;
    }

    public void uncaughtException(Thread t, Throwable e) {
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString()+"\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (int i=0; i<arr.length; i++) {
            report += "    "+arr[i].toString()+"\n";
        }
        report += "-------------------------------\n\n";

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause

        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if(cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i=0; i<arr.length; i++) {
                report += "    "+arr[i].toString()+"\n";
            }
        }
        report += "-------------------------------\n\n";

        try {
            FileOutputStream trace = app.openFileOutput("stack.trace",
                    Context.MODE_PRIVATE);
            trace.write(report.getBytes());
            trace.close();
        } catch(IOException ioe) {
            // ...
        }
sendEmail();
        defaultUEH.uncaughtException(t, e);
    }

    private void sendEmail() {
        String line="";
        String trace="";

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(app.openFileInput("stack.trace")));
            while((line = reader.readLine()) != null) {
                trace += line+"\n";
            }
        } catch(FileNotFoundException fnfe) {
            // ...
        } catch(IOException ioe) {
            // ...
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Error report";
        String body = "Mail this to er.mohittanwar94@gmail.com: " + "\n" + trace + "\n";

        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"er.mohittanwar94@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");
        app.startActivity(Intent.createChooser(sendIntent, "Title:"));

        app.deleteFile("stack.trace");
    }
}
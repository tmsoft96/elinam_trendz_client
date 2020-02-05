package com.tmsoft.tm.elinamclient.Handles;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateAndTime {
    private Calendar calendar;

    public String getDate() {
        calendar = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        return currentDateFormat.format(calendar.getTime());
    }

    public String getTime() {
        calendar = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        return currentTimeFormat.format(calendar.getTime());
    }
}

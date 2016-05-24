package com.itextpdf.io.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public final class DateTimeUtil {

    public static double getTimeInMillis(Calendar calendar) {
        if (calendar == null) {
            calendar = new GregorianCalendar();
        }
        return calendar.getTimeInMillis();
    }
}

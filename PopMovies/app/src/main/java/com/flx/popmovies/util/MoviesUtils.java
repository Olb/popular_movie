package com.flx.popmovies.util;

import android.net.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MoviesUtils {

    public static String stringToDateReport(String s){
        SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.US);
        Date date;

        try {
            date = format.parse(s);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
            return "";
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public static String ratingWithDenominator(Double numerator, String denominator) {
        return numerator + denominator;
    }

    public static String getPosterPath(String path) {
        return NetworkUtils.IMAGES_BASE_URL + path;
    }

}

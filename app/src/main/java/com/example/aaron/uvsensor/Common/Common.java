package com.example.aaron.uvsensor.Common;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static String API_KEY = "00c5a0b19994d9e5770682e74074f710";
//    public static String API_LINK = "http://api.openweathermap.org/data/2.5/uvi/forecast?appid=00c5a0b19994d9e5770682e74074f710&lat=51.2365&lon=-0.5703&cnt=5";
//    public static String UV_DAYS = "5";
    public static String API_LINK = "http://api.openweathermap.org/data/2.5/weather?appid=00c5a0b19994d9e5770682e74074f710&lat=51.2362&lon=0.5704";

    @NonNull
    public static String apiRequest(String lat, String lng){
        StringBuilder sb = new StringBuilder(API_LINK);
//        sb.append(String.format("?appid=%s&lat=%s&lon=%s&cnt=%s",API_KEY,lat,lng,UV_DAYS));
        sb.append(String.format("?appid=%s&lat=%s&lon=%s",API_KEY,lat,lng));
        return sb.toString();
    }

    public static String unixTimeStampToDateTime(double unixTimeStamp){
        DateFormat dateformat = new SimpleDateFormat("HH:MM");
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateformat.format(date);

    }

    public static String getImage(String icon){
        return String.format("https://openweathermap.org/img/w/%s.png",icon);
    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:MM");
        Date date = new Date();
        return dateFormat.format(date);
    }

}

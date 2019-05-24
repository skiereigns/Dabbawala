package com.example.dabbawala;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by inspirin on 11/13/2017.
 */

class Constants {
    public static final String DATABASE_PATH_UPLOADS = "dabba_wala";
    public static final String api_key = "AIzaSyDFqTFbwRu2puxSBj-fe61KJb1INPiuEK4";
    public static String single_chat="chat_single";
    public static String users="users";
    public static String resturant="resturants";
    public static String menu="menu";
    public static String booking_details="orders_details";
    public static String items="items";


    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(
                "yyyy/MM/dd hh:mm:ss");

        Calendar cal = Calendar.getInstance();

        return dateFormat.format(cal.getTime());// "11/03/14 12:33:43";
    }
    public static String getDateTimeID() {
        DateFormat dateFormat = new SimpleDateFormat(
                "yyyy_MM_dd_hh_mm_ss");

        Calendar cal = Calendar.getInstance();

        return dateFormat.format(cal.getTime());// "11/03/14 12:33:43";
    }
    public static long getDiff(String dated) {
        long diffDays = 0;
        try {

            Date date1 = null;
            Date date2 = null;
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM");
            String reg_date = dateFormat.format(cal.getTime());// "11/03/14 12:33:43";

            date1 = dateFormat.parse(dated);

            date2 = dateFormat.parse(reg_date);
            long diff = date2.getTime()-date1.getTime()  ;
            System.out.println("difference between days: " + diff+" "+date1.getTime()+" "+date2.getTime());
            diffDays =  diff / (24*60*60  * 1000);
            System.out.println("difference between days: " + diffDays);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return diffDays;
    }
}

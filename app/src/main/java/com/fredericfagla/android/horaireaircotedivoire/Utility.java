package com.fredericfagla.android.horaireaircotedivoire;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by frederic on 23/03/2015.
 */
public class Utility {

    //Method to add date informations to an ArrayList
    static ArrayList arrayDate(String day, String dayNo, String month, String year){
        ArrayList<String> arraySplit = new ArrayList<>();

        arraySplit.add(day.replace(",",""));//Monday
        arraySplit.add(dayNo.replace(",",""));//23
        arraySplit.add(month.replace(",",""));//March
        arraySplit.add(year.replace(",",""));//2015
        return arraySplit;
    }

    //Method to convert date string to date format and split it to an ArrayList
    static ArrayList splitDate(String date){
        Locale locale = Locale.getDefault();

        String[] tabDate = localDate(date).split(" ");
        if(String.valueOf(locale).contains("en")){
            return arrayDate(tabDate[0], tabDate[2], tabDate[1], tabDate[3]);
        }
        else{
            return arrayDate(tabDate[0], tabDate[1], tabDate[2], tabDate[3]);
        }
    }

    //Method te return locale date format
    static String localDate(String date){
        Date d = changeDateFormat(date, "dd/MM/yyyy");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        return dateFormat.format(d);
    }

   //Method to convert english date format to french date format
    public static String convertToFrenshFormat(String date){
        Date d = null;
        if(String.valueOf(Locale.getDefault()).contains("en")){
            d = changeDateFormat(date, "MM/dd/yyyy");
            Locale locale = Locale.FRENCH;
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
            return dateFormat.format(d);
        }
        else {
            return date;
        }
    }

    //Method to change date format
    public static Date changeDateFormat(String date, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return  null;
        }
    }

    //Method to convert date to string in default locale language
    public static String defaultLocalFormat(String date){
        Locale locale = Locale.getDefault();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);
        return dateFormat.format(changeDateFormat(date, "dd/MM/yyyy"));
    }
}

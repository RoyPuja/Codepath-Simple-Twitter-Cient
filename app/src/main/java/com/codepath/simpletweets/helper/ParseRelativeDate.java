package com.codepath.simpletweets.helper;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by pnroy on 10/3/15.
 */
public class ParseRelativeDate {
    private final static String twitterFormat="EEE MMM dd HH:mm:ss ZZZZ yyyy";
    public static String getRelativetimeAgo(String rawJSONDate){

        SimpleDateFormat sf=new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate="";
        try{
            long dateMillis=sf.parse(rawJSONDate).getTime();
            relativeDate= DateUtils.getRelativeTimeSpanString(dateMillis,System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS).toString();

        }catch(ParseException e){
            e.printStackTrace();

        }
        return relativeDate;
    }
}

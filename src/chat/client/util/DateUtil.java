package chat.client.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

	private static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String BRAZILIAN_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	public static final String DATE_TIME_FORMAT_TIMEZONE = "yyyy-MM-dd HH:mm:ss z";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String HOUR_FORMAT = "HH:mm:ss";
	

	public static String convertDate(Date date, String pattern){
		try {
			DateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
			simpleDateFormat.setLenient(false);
			return simpleDateFormat.format(date);
		} catch (Exception e) {
			Log.e(LogKey.ERROR, e.getMessage(), e);
		}
		return null;
	}
	
	public static String getUTCTime(Date date, String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		dateFormat.applyPattern(format);
		return dateFormat.format(date);
	}

	public static Date parseStringUTC(String dateString, String pattern) {
		try {
			DateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			simpleDateFormat.setLenient(false);
			return simpleDateFormat.parse(dateString);
		} catch (Exception e) {
			Log.e(LogKey.ERROR, e.getMessage(), e);
		}
		return null;
	}
	
	public static Date parseString(String dateString, String pattern) {
		try {
			DateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
			simpleDateFormat.setLenient(false);
			return simpleDateFormat.parse(dateString);
		} catch (Exception e) {
			Log.e(LogKey.ERROR, e.getMessage(), e);
		}
		return null;
	}
	
	public static Date convertDBFormat(String dateString){
		try {
			DateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT, Locale.getDefault());
			simpleDateFormat.setLenient(false);
			return simpleDateFormat.parse(dateString);
		} catch (Exception e) {
			Log.e(LogKey.ERROR, e.getMessage(), e);
		}
		return null;
	}
	
	public static String convertToDBFormat(Date date){
		if (date != null) {
			DateFormat simpleDateFormat = new SimpleDateFormat(DB_DATE_FORMAT, Locale.getDefault());
			simpleDateFormat.setLenient(false);
			return  simpleDateFormat.format(date);
		}
		return null;
	}

}

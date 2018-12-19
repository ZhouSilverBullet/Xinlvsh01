package com.huichenghe.bleControl.Utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtils {

	private static final String LOG_TAG = DateUtils.class.getSimpleName();
	
	public static int currentTimeSeconds() {
		int curr = (int) (System.currentTimeMillis() / 1000);
		return curr;
	}

	public void getTimeByDate() {
		Date date = new Date();
		DateFormat df1 = DateFormat.getDateInstance();		// 日期格式，精确到日
		System.out.println(df1.format(date));
		DateFormat df2 = DateFormat.getDateTimeInstance();	// 可以精确到时分秒
		System.out.println(df2.format(date));
		DateFormat df3 = DateFormat.getTimeInstance();		// 只显示出时分秒
		System.out.println(df3.format(date));
		DateFormat df4 = DateFormat.getDateTimeInstance(DateFormat.FULL,
				DateFormat.FULL); 						// 显示日期，周，上下午，时间（精确到秒）
		System.out.println(df4.format(date));
		DateFormat df5 = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG); 						// 显示日期,上下午，时间（精确到秒）
		System.out.println(df5.format(date));
		DateFormat df6 = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT); 						// 显示日期，上下午,时间（精确到分）
		System.out.println(df6.format(date));
		DateFormat df7 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM); 					// 显示日期，时间（精确到分）
		System.out.println(df7.format(date));
	}

	public static void showTimeByCalendar(Date theDate) {
		Calendar cal = Calendar.getInstance();
		if (theDate != null) {
			cal.setTime(theDate);
		}
		int year = cal.get(Calendar.YEAR);// 获取年份
		int month = cal.get(Calendar.MONTH) + 1;// 获取月份
		int day = cal.get(Calendar.DATE);// 获取日
		int pm = cal.get(Calendar.AM_PM);
		int hour = cal.get(Calendar.HOUR) + pm *12;// 小时
		int minute = cal.get(Calendar.MINUTE);// 分
		int second = cal.get(Calendar.SECOND);// 秒
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);	// 一周的第几天
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); 
		int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR); 
		Log.i("", "dateTest, 现在的时间是：公元" + year + "年" + month + "月" + day
				+ "日      " + hour + "时" + minute + "分" + second + "秒      "
				+ dayOfMonth +"   月的第 "+dayOfMonth+" 天"
				+ dayOfWeek +"   年的第 "+weekOfYear+" 周");
	}
	
//	public static int getDayOfMonth(Date theDate) {
//		Calendar cal = Calendar.getInstance();
//		if (theDate != null) {
//			cal.setTime(theDate);
//		}
//		return cal.get(Calendar.DAY_OF_MONTH);
//	}
	
	public static String getFormart_HH_MM(long theSeconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(theSeconds * 1000);
		int pm = cal.get(Calendar.AM_PM);
		int hour = cal.get(Calendar.HOUR) + pm *12;// 小时
		int minute = cal.get(Calendar.MINUTE);// 分
		String _h = ""+hour;
		if (hour < 10) {
			_h = "0"+_h;
		}
		String _m = ""+minute;
		if (minute < 10) {
			_m = "0"+_m;
		}
		String ret = ""+_h+":"+_m;
		
		Date test = DateUtils.getDateFromSec(theSeconds);
		Log.e("", "ret = "+ret+"; test = "+test.toString());
		
		return ret;
	}
	
	public static int getMonthOfYear(Date theDate) {
		Calendar cal = Calendar.getInstance();
		if (theDate != null) {
			cal.setTime(theDate);
		}
		return (cal.get(Calendar.MONTH) + 1);
	}
	
	public static int getDayOfWeek(Date theDate) {
		Calendar cal = Calendar.getInstance();
		if (theDate != null) {
			cal.setTime(theDate);
		}
		return cal.get(Calendar.DAY_OF_WEEK); 
	}
	
	public static int getDayOfYear(Date theDate) {
		Calendar cal = Calendar.getInstance();
		if (theDate != null) {
			cal.setTime(theDate);
		}
		return cal.get(Calendar.DAY_OF_YEAR); 
	}

	public static int getWeekOfYear(String the_date) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date;
		try {
			date = sdf.parse(the_date);
			cal.setTime(date);
			cal.add(Calendar.MONTH, -1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal.get(Calendar.WEEK_OF_YEAR);
	}
	
	public static int getWeekOfYear(Date theDate) {
		Calendar cal = Calendar.getInstance();
		if (theDate != null) {
			cal.setTime(theDate);
		}
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	private static Date getTodayNYR(Date theDate) {
		Calendar cal = Calendar.getInstance();
		if (theDate != null) {
			Log.e("", "getTodayNYR....theDate = "+theDate);
			cal.setTime(theDate);
		}
		int year = cal.get(Calendar.YEAR);// 获取年份
		int month = cal.get(Calendar.MONTH) + 1;// 获取月份
		int day = cal.get(Calendar.DATE);// 获取日
		String today = "" + year + "-" + month + "-" + day;

		Date ret = parseDayString(today);
		Log.e("", "getTodayNYR....ret = "+ret);
		return ret;
	}
	
	public static String getTodayNYR2Str(Date theDate) {
		Calendar cal = Calendar.getInstance();
		if (theDate != null) {
			cal.setTime(theDate);
		}
		int year = cal.get(Calendar.YEAR);// 获取年份
		int month = cal.get(Calendar.MONTH) + 1;// 获取月份
		int day = cal.get(Calendar.DATE);// 获取日
		
		String _m = ""+month;
		if (month < 10) {
			_m = "0"+_m;
		}
		String _d = ""+day;
		if (day < 10) {
			_d = "0"+_d;
		}
		
		String today = "" + year + "-" + _m + "-" + _d;

		return today;
	}

	public static Date getThedayBeginDate(Date theDate, long offset) {
		if (theDate == null) {
			theDate = getTodayNYR(null);
		}
		if (0 == offset) {
			return theDate;
		}
		long offsetSec = offset * 24 * 60 * 60 * 1000;
		long retDateSec = theDate.getTime() + offsetSec;
		Date ret = transferLongToDate(retDateSec);
		return ret;
	}
	
	public static Date getDateFromSec(long seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(seconds * 1000);
		Date date = cal.getTime();
		return date;
	}

	private static Date transferLongToDate(Long millSec) {
		return new Date(millSec);
	}

	public static Date parseDayString(String dayString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date = null;
		try {
			date = sdf.parse(dayString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getNowTime() {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return sdf.format(now);
	}

	/**
	 * 获取当前时间，并格式化，精确到毫秒值
	 * @return
	 */
	public static String getNowTimeToMill()
	{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
		return format.format(calendar.getTime());
	}
	
	public static int getAge(String birthday) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date the_date = null;
		try {
			the_date = sdf.parse(birthday);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);// 获取年份
		cal.setTime(the_date);
		cal.add(Calendar.MONTH, -1);
		int birthdayYear = cal.get(Calendar.YEAR);// 获取年份

		return (currentYear-birthdayYear);
	}

	public static String getZW(int monthOfYear) {
		String ret = "";
		switch (monthOfYear) {
			case 1:
				ret = "1";
				break;
			case 2:
				ret = "2";
				break;
			case 3:
				ret = "3";
				break;
			case 4:
				ret = "4";
				break;
			case 5:
				ret = "5";
				break;
			case 6:
				ret = "6";
				break;
			case 7:
				ret = "7";
				break;
			case 8:
				ret = "8";
				break;
			case 9:
				ret = "9";
				break;
			case 10:
				ret = "10";
				break;
			case 11:
				ret = "11";
				break;
			case 12:
				ret = "12";
				break;
		}
		return ret;
	}
	
}

package ua.stellar.seatingchart.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
	private static final DateFormat utilDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
	private static final DateFormat sqlDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat utilDateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	private static final SimpleDateFormat formatTimestam = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat timeMillisFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.US);
	private static final SimpleDateFormat formatShortTime = new SimpleDateFormat("HH:mm");
	/**
	* Convert the java.util.Date to java.sql.Date.
	* @param utilDate the time.
	* @return the converted time.
	*/
	public static final java.sql.Date utilDateToSqlDate(Date utilDate) {
		return new java.sql.Date(utilDate.getTime());
	}/**
	* Convert the java.util.Date to java.sql.Time.
	* @param utilDate the time.
	* @return the converted time.
	*/

	public static final java.sql.Time utilDateToSqlTime(Date utilDate) {
		return new java.sql.Time(utilDate.getTime());
	}

	public static final String utilDateTimeToStr(Date uDate) {
		return utilDateTimeFormatter.format(uDate);
	}
	
	/**
	* Convert the java.util.Date to java.sql.Timestamp.
	* @param utilDate the time.
	* @return the converted time.
	*/

	public static final Timestamp utilDateToSqlTimestamp(Date utilDate) {
		return new Timestamp(utilDate.getTime());
	}
		
//	public static java.sql.Date utilDateToSqlDate(java.util.Date uDate) throws ParseException {
//		return java.sql.Date.valueOf(sqlDateFormatter.format(uDate));
//	}

	public static Date sqlDateToUtilDate(java.sql.Date sDate) throws ParseException {
		return (Date)utilDateFormatter.parse(utilDateFormatter.format(sDate));
	}
	
	public static final Date stringToutilDate(String sDate) throws ParseException {
		return (Date) utilDateFormatter.parse(sDate);
	}
	
	public static final Timestamp stringToSqlTimestamp(String sDate) throws ParseException {
		return utilDateToSqlTimestamp( stringToutilDate(sDate) );
	}

	public static final Timestamp getNextDate(String sDate) throws ParseException {
		Date date = stringToutilDate(sDate);
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(cal.get(cal.YEAR),cal.get(cal.MONTH),cal.get(cal.DATE),23,59,59);
		return utilDateToSqlTimestamp(cal.getTime());
	}
	
	public static final Date getNextDateWhithoutTime(Date date, int count) throws Exception {
		GregorianCalendar cal = new GregorianCalendar();
		
		cal.setTime(date);
		cal.set(cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DATE) + count, 00, 00, 00);
		
		Date res = utilDateFormatter.parse(utilDateFormatter.format(cal.getTime()));
		return res;
	}
	
	public static final Date getInventoryDateFrom() throws Exception {
		return getNextDateWhithoutTime(new Date(), -10);
	}
	
	public static final Date getInventoryDateTo() throws Exception {
		return getNextDateWhithoutTime(new Date(), 0);
	}
	
	public static final String convertMilliSecondsToString(long value) throws Exception {
		long second = (value / 1000) % 60;
		long minute = (value / (1000 * 60)) % 60;
		long hour = (value / (1000 * 60 * 60)) % 24;
		
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	public static final String utilDateToShortTimeStr(java.util.Date uDate) {
		return formatShortTime.format(uDate);
	}
	
}

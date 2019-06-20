package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtilities {
	// ex: 13 => 1
	public static String getPeriod(LocalTime t) {
		int militaryHour = t.getHour();
		return militaryHour<12 ? "am" : "pm";
	}
	public static String getPeriod(int militaryHour) {
		return militaryHour<12 ? "am" : "pm";
	}
	public static int militaryToTwelveHour(int militaryHour) {
		return (militaryHour%12==0) ? 12 : militaryHour%12;//converts hour from military to 12-hour time
	}
	public static int getTwelveHour(LocalTime t) {
		return militaryToTwelveHour(t.getHour());
	}
	public static String formatDate(LocalDate d) {
		return d.getMonthValue() + "-" + d.getDayOfMonth() + "-" + d.getYear();
	}
	//time => 12:00am
	public static String formatComboBoxTime(LocalTime t) {
		int hour = getTwelveHour(t);
		String minutes = t.getMinute() < 10 ? ":0" + t.getMinute() : ":" + t.getMinute(); 
		String period = getPeriod(t);
		return hour + minutes + period;
	}
	//time => 12am
	public static String formatTime(LocalTime t) {
		int hour = getTwelveHour(t);
		String minutes = "";
		if (t.getMinute()!=0) {
			minutes = t.getMinute() < 10 ? ":0" + t.getMinute() : ":" + t.getMinute(); 
		}
		String period = getPeriod(t);
		return hour + minutes + period;
	}
	//12-1pm, 8am-1:30pm, etc
	public static String formatTimeRange(LocalTime start, LocalTime end) {
		//end formatting
		int endHour = getTwelveHour(end);
		String endMinutes = "";
		if (end.getMinute()!=0) {
			endMinutes = end.getMinute() < 10 ? ":0" + end.getMinute() : ":" + end.getMinute(); 
		}
		String endPeriod = getPeriod(end);
		//start formatting
		int startHour = getTwelveHour(start); 
		String startMinutes = "";
		if (start.getMinute()!=0) {
			startMinutes = start.getMinute() < 10 ? ":0" + start.getMinute() : ":" + start.getMinute(); 
		}
		//don't specifiy startperiod if it is the same as the end period i.e. 1-2pm
		String startPeriod = getPeriod(start).equals(endPeriod) ? "" : getPeriod(start);
		//return output
		return startHour + startMinutes + startPeriod + " - " + endHour + endMinutes + endPeriod;
	}
	//same date: Monday, date, time - time
	//different date: date, time - date, time
	public static String formatDateTimeRange(LocalDateTime start, LocalDateTime end) {
		LocalDate startDate = start.toLocalDate();
		LocalDate endDate = end.toLocalDate();
		LocalTime startTime = start.toLocalTime();
		LocalTime endTime = end.toLocalTime();
		if (startDate.equals(endDate)) {
			String dayOfWeek = startDate.getDayOfWeek().toString().toLowerCase();
			String dayOfWeekCapitalized = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1);
			return dayOfWeekCapitalized + ", " + formatDate(startDate) + ", "
				+ formatTimeRange(startTime, endTime);
		}else {
			return formatDate(startDate) + ", " + formatTime(startTime) + " - " + formatDate(endDate) + ", " + formatTime(endTime);
		}
	}
	
}

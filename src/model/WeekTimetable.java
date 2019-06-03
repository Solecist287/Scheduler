package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;

public class WeekTimetable extends HourlyTimetable {

	public WeekTimetable(CalendarModel cmodel, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(cmodel, initDate, 7, dayOfWeekTemporalField);
	}

	@Override
	public void updateView(LocalDate date) {
		for (int i = 1; i <= 7; i++) {
			LocalDate currentDate = date.with(dayOfWeekTemporalField,i);
			String day = currentDate.getDayOfWeek().toString().substring(0,3).toLowerCase();
			System.out.println("date: " + currentDate + " day: " + day + " i: " + i);
		}
	}

	@Override
	public String toString() {
		return "Week";
	}

}

package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;

public class DayTimetable extends HourlyTimetable {

	public DayTimetable(CalendarModel cmodel, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(cmodel, initDate, 1, dayOfWeekTemporalField);
	}

	@Override
	public void updateView(LocalDate date) {
		
	}

	@Override
	public String toString() {
		return "Day";
	}

}

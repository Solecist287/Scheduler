package model;

import java.time.LocalDate;

public class WeekTimetable extends HourlyTimetable {

	public WeekTimetable(CalendarModel cmodel, LocalDate initDate) {
		super(cmodel, initDate, 7);
	}

	@Override
	public void updateView(LocalDate date) {
	}

	@Override
	public String toString() {
		return "Week";
	}

}

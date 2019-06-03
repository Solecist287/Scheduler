package model;

import java.time.LocalDate;

public class DayTimetable extends HourlyTimetable {

	public DayTimetable(CalendarModel cmodel, LocalDate initDate) {
		super(cmodel, initDate, 1);
	}

	@Override
	public void updateView(LocalDate date) {
		
	}

	@Override
	public String toString() {
		return "Day";
	}

}

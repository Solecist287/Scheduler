package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;

import javafx.scene.Node;

public class YearTimetable extends Timetable {

	public YearTimetable(CalendarModel cmodel, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(cmodel, initDate, dayOfWeekTemporalField);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Node createView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(LocalDate date) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "Year";
	}

}

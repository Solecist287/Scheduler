package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class YearTimetable extends Timetable {

	public YearTimetable(ObservableList<Event> events, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(events, initDate, dayOfWeekTemporalField);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createView() {
		view = null;
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

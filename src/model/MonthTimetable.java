package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class MonthTimetable extends Timetable {

	public MonthTimetable(LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(initDate, dayOfWeekTemporalField);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createView() {
		view = null;
	}

	@Override
	public void update(LocalDate date, List<Event> events) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "Month";
	}

	@Override
	public void addEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

}

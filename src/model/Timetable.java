package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;

import javafx.scene.Node;
import javafx.collections.ObservableList;

public abstract class Timetable {
	public ObservableList<Event> events;
	Node view;
	TemporalField dayOfWeekTemporalField;
	public static final int WIDTH = 742;
	LocalDate lastDateEntered;//highlighted date, if applicable
	public Timetable(LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		this.events = events;
		lastDateEntered = initDate;
		this.dayOfWeekTemporalField = dayOfWeekTemporalField;
		view = null;
	}
	public Node getView() {
		return view;
	}
	public abstract void createView();
	//refreshes rows/cols if applicable and highlights correct date if applicable
	public abstract void update(LocalDate date, List<Event> events);
	public abstract void addEvent(Event e);
	public abstract void removeEvent(Event e);
	public abstract String toString();//displays text for timeunitcombobox
}

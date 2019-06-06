package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;

import javafx.scene.Node;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class Timetable {
	public ObservableList<Event> events;
	Node view;
	TemporalField dayOfWeekTemporalField;
	public static final int WIDTH = 742;
	LocalDate lastDateEntered;//highlighted date, if applicable
	public Timetable(ObservableList<Event> events, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
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
	public abstract void update(LocalDate date);
	
	public abstract String toString();//displays text for timeunitcombobox
}

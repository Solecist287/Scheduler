package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;

import javafx.scene.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Timetable {
	CalendarModel cmodel;
	ObservableList<Event> events;
	Node view;
	TemporalField dayOfWeekTemporalField;
	public static final int WIDTH = 742;
	LocalDate lastDateEntered;//highlighted date, if applicable
	public Timetable(CalendarModel cmodel, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		this.cmodel = cmodel;
		events = FXCollections.observableList(cmodel.getEvents());
		lastDateEntered = initDate;
		this.dayOfWeekTemporalField = dayOfWeekTemporalField;
		view = null;
	}
	public Node getView() {
		return view;
	}
	public abstract Node createView();
	//refreshes rows/cols if applicable and highlights correct date if applicable
	public abstract void updateView(LocalDate date);
	
	public abstract String toString();//displays text for timeunitcombobox
}

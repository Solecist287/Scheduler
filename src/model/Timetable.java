package model;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;

public abstract class Timetable {
	CalendarModel cmodel;
	ObservableList<Event> events;
	Node view;
	final int width = 742;
	LocalDate lastDateEntered;//highlighted date, if applicable
	public Timetable(CalendarModel cmodel, LocalDate initDate) {
		this.cmodel = cmodel;
		events = FXCollections.observableList(cmodel.getEvents());
		lastDateEntered = initDate;
		view = null;
	}
	public int getWidth() {
		return width;
	}
	public Node getView() {
		return view;
	}
	public abstract Node createView();
	public abstract void updateView(LocalDate date);//refreshes rows/cols if applicable and highlights correct date if applicable
	public abstract String toString();//displays text for timeunitcombobox
}

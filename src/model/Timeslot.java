package model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Timeslot {
	private LocalDateTime startDateTime, endDateTime;
	private Event e;
	private HBox view;
	private Label l;
	private final int MIN_EVENT_TIME = 35;
	public Timeslot(Event e, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this.e = e;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		Color c = e.getBackgroundColor();
		view = new HBox();
		view.setStyle("-fx-background-color: rgb(" + c.getRed()*255 + "," + c.getGreen()*255 + "," + c.getBlue()*255 + ");"
				+ "-fx-border-color: black;" + "-fx-font-size: 12");
		//make sure event is at least minimum event time length (30 min)
		if (startDateTime.until(e.getEndDateTime(), ChronoUnit.MINUTES) >= MIN_EVENT_TIME) {
			l = new Label(e.getTitle() + "\n" 
					+ TimeUtilities.formatTimeRange(e.getStartDateTime().toLocalTime(), e.getEndDateTime().toLocalTime()));
			view.getChildren().add(l);
		}
	}
	public Event getEvent() {
		return e;
	}
	public HBox getView() {
		return view;
	}
	public int getRowSpan() {
		return (int)startDateTime.until(endDateTime, ChronoUnit.MINUTES)/5;
	}
	public int getRow() {
		LocalTime startTime = startDateTime.toLocalTime();
		return (startTime.getHour() * 60 + startTime.getMinute())/5;
	}

}

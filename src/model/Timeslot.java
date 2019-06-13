package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Timeslot {
	private LocalDateTime startDateTime, endDateTime;
	private Event e;
	private HBox h;
	private Label l;
	private final int MIN_EVENT_TIME = 35;
	public Timeslot(Event e, LocalDateTime startDateTime, LocalDateTime endDateTime) {
		this.e = e;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		Color c = e.getBackgroundColor();
		h = new HBox();
		h.setStyle("-fx-background-color: rgb(" + c.getRed()*255 + "," + c.getGreen()*255 + "," + c.getBlue()*255 + ");"
				+ "-fx-border-color: black;" + "-fx-font-size: 12");
		//make sure event is not <30 min
		if (Duration.between(startDateTime, e.getEndDateTime()).toMinutes() >= MIN_EVENT_TIME) {
			l = new Label(e.getTitle() + "\n" + e.getStartDateTime().toLocalTime() + "-" + e.getEndDateTime().toLocalTime());
			h.getChildren().add(l);
		}
	}
	public Event getEvent() {
		return e;
	}
	public HBox getView() {
		return h;
	}
	public int getRowSpan() {
		return (int)(Duration.between(startDateTime, endDateTime).abs().toMinutes())/5;
	}
	public int getRow() {
		LocalTime startTime = startDateTime.toLocalTime();
		return (startTime.getHour() * 60 + startTime.getMinute())/5;
	}

}

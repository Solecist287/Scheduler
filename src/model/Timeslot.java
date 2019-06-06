package model;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Timeslot {
	private Event e;
	private HBox h;
	private Label l;
	public Timeslot(Event e) {
		this.e = e;
		Color c = e.getBackgroundColor();
		h = new HBox();
		h.setStyle("-fx-background-color: rgb(" + c.getRed()*255 + "," + c.getGreen()*255 + "," + c.getBlue()*255 + ");"
				+ "-fx-border-color: black;");
		
		//label: "starttime am/pm - endtime am/pm"
		/*
		int militaryStartHour = e.getStartDateTime().getHour();
		int startHour = (militaryStartHour%12==0) ? 12 : militaryStartHour%12;//converts hour from military to 12-hour time
		String startPeriod = militaryStartHour<12 ? "am" : "pm";
		int militaryEndHour = e.getEndDateTime().getHour();
		int endHour = (militaryEndHour%12==0) ? 12 : militaryEndHour%12;//converts hour from military to 12-hour time
		String endPeriod = militaryEndHour<12 ? "am" : "pm";
		*/
		l = new Label(e.getTitle() + "\n" + e.getStartDateTime().toLocalTime() + "-" + e.getEndDateTime().toLocalTime());
		h.getChildren().add(l);
	}
	public Event getEvent() {
		return e;
	}
	public HBox getView() {
		return h;
	}

}

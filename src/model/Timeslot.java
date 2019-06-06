package model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Timeslot {
	private Event e;
	private Pane p;
	public Timeslot(Event e) {
		p = new Pane();
		this.e = e;
		Color c = e.getBackgroundColor();
		p.setStyle("-fx-background-color: rgb(" + c.getRed()*255 + "," + c.getGreen()*255 + "," + c.getBlue()*255 + ");"
				+ "-fx-border-color: black;");
	}
	public Event getEvent() {
		return e;
	}
	public Pane getPane() {
		return p;
	}

}

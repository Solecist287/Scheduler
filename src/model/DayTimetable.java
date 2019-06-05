package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;


import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DayTimetable extends HourlyTimetable {

	public DayTimetable(CalendarModel cmodel, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(cmodel, initDate, 1, dayOfWeekTemporalField);
	}

	@Override
	public void update(LocalDate date) {
		if (!lastDateEntered.equals(date)) {
			//draw events that match date
			for (int i = 0; i < events.size(); i++) {
				Event e = events.get(i);
				if (e.getStartDateTime().toLocalDate().equals(date)) {
					Pane p = new Pane();
					Color c = e.getBackgroundColor();
					p.setStyle("-fx-background-color: rgb(" + c.getRed()*255 + "," + c.getGreen()*255 + "," + c.getBlue()*255 + ");");
					hourlyGrid.add(p, 1, e.getRow(), 1, e.getRowSpan());
				}
			}
		}
		lastDateEntered = date;
	}

	@Override
	public String toString() {
		return "Day";
	}

}

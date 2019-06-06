package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DayTimetable extends HourlyTimetable {

	public DayTimetable(LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(initDate, 1, dayOfWeekTemporalField);
	}

	@Override
	public void update(LocalDate date, List<Event> events) {
		//recreate view
		createView();
		//clear timeslots
		timeslots.clear();
		//draw events that match date
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			//search linearly until after last day entered
			if (e.getStartDateTime().toLocalDate().isAfter(date)) {
				break;//stop searching
			//add events on this input date
			}else if (e.getStartDateTime().toLocalDate().equals(date)) {
				Timeslot newTimeslot = new Timeslot(e);
				timeslots.add(newTimeslot);
				hourlyGrid.add(newTimeslot.getPane(), 1, e.getRow(), 1, e.getRowSpan());
				//hourlyGrid.getChildren().remove(p);
			}
		}
		lastDateEntered = date;
	}
	
	@Override
	public void addEvent(Event e) {
		if (e.getStartDateTime().toLocalDate().equals(lastDateEntered)) {
			Timeslot t = new Timeslot(e);
			timeslots.add(t);
			hourlyGrid.add(t.getPane(), 1, e.getRow(), 1, e.getRowSpan());
		}
	}

	@Override
	public void removeEvent(Event e) {
		if (e.getStartDateTime().toLocalDate().equals(lastDateEntered)) {
			for (int i = 0; i < timeslots.size(); i++) {
        		Timeslot t = timeslots.get(i);
        		if (e.equals(t.getEvent())) {
        			hourlyGrid.getChildren().remove(t.getPane());
        			timeslots.remove(t);
        			break;
        		}
        	}
		}
	}
	
	@Override
	public String toString() {
		return "Day";
	}
}

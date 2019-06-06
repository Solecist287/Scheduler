package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DayTimetable extends HourlyTimetable {

	public DayTimetable(ObservableList<Event> events, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(events, initDate, 1, dayOfWeekTemporalField);
		events.addListener((ListChangeListener<Event>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                	for (Event e : change.getAddedSubList()) {
                		Timeslot t = new Timeslot(e);
                		timeslots.add(t);
                		hourlyGrid.add(t.getPane(), 1, e.getRow(), 1, e.getRowSpan());
                	}
                } else if (change.wasRemoved()) {
                    for (Event e : change.getRemoved()) {
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
            }
        });
	}

	@Override
	public void update(LocalDate date) {
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
				//for (int k = e.getRow(); k < e.getRow() + e.getRowSpan(); k++) {
				//	hourlyGrid.getChildren().remove(k,i);
				//}
				Timeslot newTimeslot = new Timeslot(e);
				timeslots.add(newTimeslot);
				hourlyGrid.add(newTimeslot.getPane(), 1, e.getRow(), 1, e.getRowSpan());
				//hourlyGrid.getChildren().remove(p);
			}
		}
		lastDateEntered = date;
	}
	
	@Override
	public String toString() {
		return "Day";
	}

}

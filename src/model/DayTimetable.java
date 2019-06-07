package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class DayTimetable extends HourlyTimetable {

	public DayTimetable(List<Event> events, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(events, initDate, 1, dayOfWeekTemporalField);
	}

	@Override
	public void update(LocalDate date) {
		if (!date.equals(lastDateEntered)) {
			//clear timeslots from list and hourlygrid
			clearTimeslots();
			//draw events that match date
			for (int i = 0; i < events.size(); i++) {
				Event e = events.get(i);
				//search linearly until after last day entered
				if (e.getStartDateTime().toLocalDate().isAfter(date)) {
					break;//stop searching
				//add events on this input date
				}else if (e.getStartDateTime().toLocalDate().equals(date)) {
					Timeslot t = new Timeslot(e);
					timeslots.add(t);
					t.getView().setOnMouseClicked(event -> {
						System.out.println("deleted: " + t.getEvent().getStartDateTime());
						events.remove(e);
					});
					hourlyGrid.add(t.getView(), 1, e.getRow(), 1, e.getRowSpan());
					//hourlyGrid.getChildren().remove(p);
				}
			}
		}
		lastDateEntered = date;
	}
	
	@Override
	public void addEvent(Event e) {
		if (e.getStartDateTime().toLocalDate().equals(lastDateEntered)) {
			Timeslot t = new Timeslot(e);
			timeslots.add(t);
			Node view = t.getView();
			view.setOnMouseClicked(event -> {
				System.out.println("deleted: " + t.getEvent().getStartDateTime());
				events.remove(e);
			});
			hourlyGrid.add(view, 1, e.getRow(), 1, e.getRowSpan());
		}
	}

	@Override
	public void removeEvent(Event e) {
		if (e.getStartDateTime().toLocalDate().equals(lastDateEntered)) {
			for (int i = 0; i < timeslots.size(); i++) {
        		Timeslot t = timeslots.get(i);
        		if (e.equals(t.getEvent())) {
        			hourlyGrid.getChildren().remove(t.getView());
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

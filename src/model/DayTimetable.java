package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;
import javafx.scene.Node;
import javafx.stage.Stage;

public class DayTimetable extends HourlyTimetable {

	public DayTimetable(Stage mainStage, List<Event> events, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(mainStage, events, initDate, 1, dayOfWeekTemporalField);
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
					addTimeslot(e);
				}
			}
		}
		lastDateEntered = date;
	}
	
	@Override
	public void addEvent(Event e) {
		if (e.getStartDateTime().toLocalDate().equals(lastDateEntered)) {
			addTimeslot(e);
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

	@Override
	public void addTimeslot(Event e) {
		Timeslot t = new Timeslot(e);
		timeslots.add(t);
		Node view = t.getView();
		view.setOnMouseClicked(event -> {
			modifyEventPopup(e);
		});
		hourlyGrid.add(view, 1, e.getRow(), 1, e.getRowSpan());
	}
}

package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;

public class DayTimetable extends HourlyTimetable {

	public DayTimetable(LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(initDate, 1, dayOfWeekTemporalField);
	}

	@Override
	public void update(LocalDate date, List<Event> events) {
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
				hourlyGrid.add(t.getView(), 1, e.getRow(), 1, e.getRowSpan());
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
			hourlyGrid.add(t.getView(), 1, e.getRow(), 1, e.getRowSpan());
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

package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
				//add events if it doesn't end before input date
				}else if (isRenderable(e,date)) {
					addTimeslots(e, date);
				}
			}
		}
		lastDateEntered = date;
	}
	
	@Override
	public void addEvent(Event e) {
		if (isRenderable(e,lastDateEntered)) {
			addTimeslots(e, lastDateEntered);
		}
	}

	@Override
	public void removeEvent(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			for (int i = 0; i < timeslots.size(); i++) {
        		Timeslot t = timeslots.get(i);
        		if (e.equals(t.getEvent())) {
        			hourlyGrid.getChildren().remove(t.getView());
        			timeslots.remove(t);
        			i--;
        		}
        	}
		}
	}
	
	@Override
	public String toString() {
		return "Day";
	}

	@Override
	public void addTimeslots(Event e, LocalDate date) {
		//make day slice of event for singular timeslot
		LocalDateTime timeslotStart = (e.getStartDateTime().toLocalDate().isBefore(date))
				? LocalDateTime.of(date, LocalTime.MIDNIGHT) : e.getStartDateTime();
		LocalDateTime timeslotEnd = (e.getEndDateTime().toLocalDate().isAfter(date)) 
				? LocalDateTime.of(date.plusDays(1), LocalTime.MIDNIGHT) : e.getEndDateTime();
		System.out.println("timeslot");
		Timeslot t = new Timeslot(e, timeslotStart, timeslotEnd);
		timeslots.add(t);
		Node view = t.getView();
		view.setOnMouseClicked(event -> {
			modifyEventPopup(e);
		});
		hourlyGrid.add(view, 1, t.getRow(), 1, t.getRowSpan());
	}

	@Override
	public boolean isRenderable(Event e, LocalDate d) {
		LocalDateTime start = e.getStartDateTime();
		LocalDateTime end = e.getEndDateTime();
		LocalDate tomorrow = d.plusDays(1);
		LocalDate yesterday = d.minusDays(1);
		return start.toLocalDate().isBefore(tomorrow) && end.toLocalDate().isAfter(yesterday) 
				&& !end.equals(LocalDateTime.of(d, LocalTime.MIDNIGHT));
	}
}

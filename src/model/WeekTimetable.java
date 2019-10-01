package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.CalendarModel.Event;

public class WeekTimetable extends HourlyTimetable {

	public WeekTimetable(Stage mainStage, List<Event> events, LocalDate initDate, Locale locale) {
		super(mainStage, events, initDate, 7, locale);
		updateHeaderLabels(initDate);
		renderEventVisuals(initDate);
	}
	
	//cannot be static as long as temporalfield needs to be instantiated
	public boolean inTheSameWeek(LocalDate firstDate, LocalDate secondDate) {
		//set endpoints before and after week of firstDate
		LocalDate beforeStartOfWeek = firstDate.with(dayOfWeekTemporalField,1).minusDays(1);
		LocalDate afterEndOfWeek = firstDate.with(dayOfWeekTemporalField,7).plusDays(1);
		return secondDate.isAfter(beforeStartOfWeek) && secondDate.isBefore(afterEndOfWeek);
	}
	
	@Override
	public void update(LocalDate d) {
		//only do render new week if date is in another week than lastDateEntered
		if (!inTheSameWeek(lastDateEntered, d)) {
			//update day labels
			updateHeaderLabels(d);
			//clear timeslots from list and hourlygrid
			clearAllEventVisuals();
			//render events
			renderEventVisuals(d);
		}
		lastDateEntered = d;
	}

	@Override
	public void onEventAdded(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			addVisuals(e, lastDateEntered);
		}
	}

	@Override
	public void onEventRemoved(Event e) {
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
		return "Week";
	}

	@Override
	public void addVisuals(Event e, LocalDate d) {
		LocalDate startOfWeek = d.with(dayOfWeekTemporalField,1);
		LocalDate endOfWeek = d.with(dayOfWeekTemporalField,7);
		LocalDateTime timeslotStart = (e.getStartDateTime().toLocalDate().isBefore(startOfWeek))
				? LocalDateTime.of(startOfWeek, LocalTime.MIDNIGHT) : e.getStartDateTime();
		LocalDateTime end = (e.getEndDateTime().toLocalDate().isAfter(endOfWeek)) 
				? LocalDateTime.of(endOfWeek.plusDays(1), LocalTime.MIDNIGHT) : e.getEndDateTime();//upper bound
		//make timeslot slices of event for each day that the event spans
		do {	
			int weekIndex = timeslotStart.get(dayOfWeekTemporalField);
			LocalDateTime timeslotEnd = (end.toLocalDate().isAfter(timeslotStart.toLocalDate())) 
					? timeslotStart.plusDays(1).with(LocalTime.MIDNIGHT) : end;
			Timeslot t = new Timeslot(e, timeslotStart, timeslotEnd);
			timeslots.add(t);
			Node view = t.getView();
			view.setOnMouseClicked(event -> {
				viewEventPopup(e);
			});
			hourlyGrid.add(view, weekIndex, t.getRow(), 1, t.getRowSpan());
			timeslotStart = timeslotStart.plusDays(1).with(LocalTime.MIDNIGHT);
		}while(timeslotStart.isBefore(end));
	}

	@Override
	public boolean isRenderable(Event e, LocalDate d) {
		LocalDateTime start = e.getStartDateTime();
		LocalDateTime end = e.getEndDateTime();
		LocalDate beforeStartOfWeek = d.with(dayOfWeekTemporalField,1).minusDays(1);
		LocalDate afterEndOfWeek = d.with(dayOfWeekTemporalField,7).plusDays(1);
		return start.toLocalDate().isBefore(afterEndOfWeek) && end.toLocalDate().isAfter(beforeStartOfWeek) 
				&& !end.equals(LocalDateTime.of(beforeStartOfWeek.plusDays(1), LocalTime.MIDNIGHT));
	}
	
	private void updateHeaderLabels(LocalDate d) {
		for (int i = 0; i < headerLabels.size(); i++) {
			LocalDate currentDate = d.with(dayOfWeekTemporalField,i+1);//day indices start at 1
			Label currentLabel = headerLabels.get(i);
			//get info for label text
			String dayOfWeekCaps = currentDate.getDayOfWeek().toString();
			String dayOfWeek = dayOfWeekCaps.substring(0,1) + dayOfWeekCaps.toLowerCase().substring(1,3);
			int dayOfMonth = currentDate.getDayOfMonth();
			//set label text
			currentLabel.setText(dayOfWeek + "\n" + dayOfMonth);
		}
	}

	@Override
	public void renderEventVisuals(LocalDate d) {
		LocalDate endOfWeek = d.with(dayOfWeekTemporalField,7);
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			//stop searching once past the end of the week
			if (e.getStartDateTime().toLocalDate().isAfter(endOfWeek)) {
				break;//stop searching
			//add timeslot if falls in the current week or intersects week
			}else if (isRenderable(e,d)) {
				addVisuals(e, d);
			}
		}
	}

}

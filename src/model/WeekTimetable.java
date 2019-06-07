package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;

public class WeekTimetable extends HourlyTimetable {

	public WeekTimetable(LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(initDate, 7, dayOfWeekTemporalField);
	}
	
	//cannot be static as long as temporalfield needs to be instantiated
	public boolean inTheSameWeek(LocalDate firstDate, LocalDate secondDate) {
		//set endpoints before and after week of firstDate
		LocalDate beforeStartOfWeek = firstDate.with(dayOfWeekTemporalField,1).minusDays(1);
		LocalDate afterEndOfWeek = firstDate.with(dayOfWeekTemporalField,7).plusDays(1);
		//return whether or not secondDate is in week of firstDate
		//System.out.println("firstDate: " + firstDate + ", secondDate: " + secondDate + 
		//		"bool: " + (secondDate.isAfter(beforeStartOfWeek) && secondDate.isBefore(afterEndOfWeek)));
		return secondDate.isAfter(beforeStartOfWeek) && secondDate.isBefore(afterEndOfWeek);
	}
	
	@Override
	public void update(LocalDate date, List<Event> events) {
		//only do render new week if date is in another week than lastDateEntered
		if (!inTheSameWeek(lastDateEntered, date)) {
			//clear timeslots from list and hourlygrid
			clearTimeslots();
			LocalDate startOfWeek = date.with(dayOfWeekTemporalField,1);
			LocalDate endOfWeek = date.with(dayOfWeekTemporalField,7);
			for (int i = 0; i < events.size(); i++) {
				Event e = events.get(i);
				//stop searching once past the end of the week
				if (e.getStartDateTime().toLocalDate().isAfter(endOfWeek)) {
					break;//stop searching
				//add timeslot if falls in the current week
				}else if (e.getStartDateTime().toLocalDate().isAfter(startOfWeek)) {
					int weekIndex = e.getStartDateTime().get(dayOfWeekTemporalField);
					//System.out.println("weekIndex: " + weekIndex);
					Timeslot t = new Timeslot(e);
					timeslots.add(t);
					hourlyGrid.add(t.getView(), weekIndex, e.getRow(), 1, e.getRowSpan());
					//hourlyGrid.getChildren().remove(p);
				}
			}
		}
		lastDateEntered = date;
	}

	@Override
	public void addEvent(Event e) {
		if (inTheSameWeek(e.getStartDateTime().toLocalDate(),lastDateEntered)) {
			int weekIndex = e.getStartDateTime().get(dayOfWeekTemporalField);
			Timeslot t = new Timeslot(e);
			timeslots.add(t);
			hourlyGrid.add(t.getView(), weekIndex, e.getRow(), 1, e.getRowSpan());
		}
	}

	@Override
	public void removeEvent(Event e) {
		if (inTheSameWeek(e.getStartDateTime().toLocalDate(),lastDateEntered)) {
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
		return "Week";
	}

}

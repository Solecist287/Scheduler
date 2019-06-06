package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;

import javafx.collections.ObservableList;

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
			//logic
		}
		lastDateEntered = date;
	}

	@Override
	public String toString() {
		return "Week";
	}

	@Override
	public void addEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEvent(Event e) {
		// TODO Auto-generated method stub
		
	}

}

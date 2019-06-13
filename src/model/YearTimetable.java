package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;

import javafx.stage.Stage;

public class YearTimetable extends Timetable {

	public YearTimetable(Stage mainStage, List<Event> events, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(mainStage, events, initDate, dayOfWeekTemporalField);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createView() {
		view = null;
	}

	@Override
	public void update(LocalDate date) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "Year";
	}

	@Override
	public void onEventAdded(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEventRemoved(Event e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderEvents(LocalDate date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRenderable(Event e, LocalDate d) {
		// TODO Auto-generated method stub
		return false;
	}

}

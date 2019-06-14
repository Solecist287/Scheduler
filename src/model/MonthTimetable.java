package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.List;

import javafx.stage.Stage;

public class MonthTimetable extends Timetable {

	public MonthTimetable(Stage mainStage, List<Event> events, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(mainStage, events, initDate, dayOfWeekTemporalField);
	}

	@Override
	public void createView() {
		view = null;
	}

	@Override
	public void update(LocalDate d) {
		
	}

	@Override
	public String toString() {
		return "Month";
	}

	@Override
	public void onEventAdded(Event e) {
		
	}

	@Override
	public void onEventRemoved(Event e) {
		
	}

	@Override
	public void renderEventNodes(LocalDate d) {
		
	}

	@Override
	public boolean isRenderable(Event e, LocalDate d) {
		return false;
	}

	@Override
	public void clearAllEventNodes() {
		
	}

	@Override
	public void addNodes(Event e, LocalDate d) {
		
	}

}

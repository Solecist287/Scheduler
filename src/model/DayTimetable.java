package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DayTimetable extends HourlyTimetable {
	private Label headerLabel;
	
	public DayTimetable(Stage mainStage, List<Event> events, LocalDate initDate, Locale locale) {
		super(mainStage, events, initDate, 1, locale);
		headerLabel = headerLabels.get(0);
		updateHeaderLabel(initDate);
		renderEventNodes(initDate);
	}

	@Override
	public void update(LocalDate d) {
		if (!d.equals(lastDateEntered)) {
			//update day label
			updateHeaderLabel(d);
			//clear timeslots from list and hourlygrid
			clearAllEventNodes();
			//draw events that match date
			renderEventNodes(d);
		}
		lastDateEntered = d;
	}
	
	@Override
	public void onEventAdded(Event e) {
		if (isRenderable(e,lastDateEntered)) {
			addNodes(e, lastDateEntered);
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
		return "Day";
	}

	@Override
	public void addNodes(Event e, LocalDate d) {
		//make day slice of event for singular timeslot
		LocalDateTime timeslotStart = (e.getStartDateTime().toLocalDate().isBefore(d))
				? LocalDateTime.of(d, LocalTime.MIDNIGHT) : e.getStartDateTime();
		LocalDateTime timeslotEnd = (e.getEndDateTime().toLocalDate().isAfter(d)) 
				? LocalDateTime.of(d.plusDays(1), LocalTime.MIDNIGHT) : e.getEndDateTime();
		System.out.println("timeslot");
		Timeslot t = new Timeslot(e, timeslotStart, timeslotEnd);
		timeslots.add(t);
		Node view = t.getView();
		view.setOnMouseClicked(event -> {
			viewEventPopup(e);
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
	
	private void updateHeaderLabel(LocalDate d) {
		String dayOfWeekCaps = d.getDayOfWeek().toString();
		String dayOfWeek = dayOfWeekCaps.substring(0,1) + dayOfWeekCaps.toLowerCase().substring(1,3);
		int dayOfMonth = d.getDayOfMonth();
		headerLabel.setText(dayOfWeek + "\n" + dayOfMonth);
	}

	@Override
	public void renderEventNodes(LocalDate d) {
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			//search linearly until after last day entered
			if (e.getStartDateTime().toLocalDate().isAfter(d)) {
				break;//stop searching
			//add events if it doesn't end before input date
			}else if (isRenderable(e,d)) {
				addNodes(e, d);
			}
		}
	}
}

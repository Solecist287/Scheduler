package controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Event;

public class ModifyEventPopupController {
	
	@FXML
	TextField titleTextField;
	@FXML
	DatePicker startDatePicker, endDatePicker;
	@FXML
	ComboBox<LocalTime> startTimeComboBox, endTimeComboBox;
	@FXML
	TextArea descriptionTextArea;
	@FXML
	ColorPicker backgroundColorPicker;
	@FXML
	Button saveButton, deleteButton;
	
	private Event selectedEvent;
	private List<Event> events;
	private Stage primaryStage;
	private int comboBoxWidth = 77;//specific, but perfectly fits time choices
	private final int TIME_INCREMENT = 5;
	
	public void start(Stage primaryStage, List<Event> events, Event e) {
		this.primaryStage = primaryStage;
		this.events = events;
		this.selectedEvent = e;
		LocalTime t = LocalTime.MIDNIGHT;
		//fill ComboBoxes with time choices for event
		do {
			startTimeComboBox.getItems().add(t);
			endTimeComboBox.getItems().add(t);
			t = t.plusMinutes(TIME_INCREMENT);
		} while(!t.equals(LocalTime.MIDNIGHT));
		//set ComboBox widths
		startTimeComboBox.setStyle("-fx-pref-width: " + comboBoxWidth);
		endTimeComboBox.setStyle("-fx-pref-width: " + comboBoxWidth);
		//set title
		titleTextField.setText(e.getTitle());
		//set date
		startDatePicker.setValue(e.getStartDateTime().toLocalDate());
		startDatePicker.setShowWeekNumbers(false);
		endDatePicker.setValue(e.getEndDateTime().toLocalDate());
		endDatePicker.setShowWeekNumbers(false);
		//set comboboxes to event starttime
		startTimeComboBox.getSelectionModel().select(e.getStartDateTime().toLocalTime());
		endTimeComboBox.getSelectionModel().select(e.getEndDateTime().toLocalTime());
		//set description
		descriptionTextArea.setText(e.getDescription());
		//set color
		backgroundColorPicker.setValue(e.getBackgroundColor());
	}
	@FXML
	public void saveEvent() {
		System.out.println("save");
		//fields for event object
		String title = titleTextField.getText();
		String description = descriptionTextArea.getText();
		LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startTimeComboBox.getValue());
		LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endTimeComboBox.getValue());
		Color color = backgroundColorPicker.getValue();
		//check if title is empty
		if (title.isEmpty() || title == null) {return;}
		//make sure startdatetime is before enddatetime (cannot be equal either), does not allow events span days
		if (!startDateTime.isBefore(endDateTime)) {return;}
		//remove old event
		events.remove(selectedEvent);
		//create new event
		Event newEvent = new Event(title, description, startDateTime, endDateTime, color);
		//check where to place event and do no insert if it collides with other event(s)
		if (events.size() == 0) {//easy case: insert in empty list
			events.add(newEvent);
			primaryStage.close();
			return;
		}
		int i = 0;
		Event currentEvent = events.get(i);
		while(!newEvent.isDuring(currentEvent) && i<events.size()) {
			if (newEvent.endsBy(currentEvent)) {
				events.add(i,newEvent);
				primaryStage.close();
				break;
			}else if (i == events.size()-1 && newEvent.startsBy(currentEvent)) {//after last event
				events.add(newEvent);
				primaryStage.close();
				break;
			}
			i++;
			currentEvent = events.get(i);
		}
	}
	
	@FXML
	public void deleteEvent() {
		System.out.println("delete");
		events.remove(selectedEvent);
		primaryStage.close();
	}
}

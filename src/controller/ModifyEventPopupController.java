package controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Event;
import model.TimeUtilities;

public class ModifyEventPopupController {
	
	@FXML
	TextField titleTextField;
	@FXML
	DatePicker startDatePicker, endDatePicker;
	@FXML
	ComboBox<String> startTimeComboBox, endTimeComboBox;
	@FXML
	TextArea descriptionTextArea;
	@FXML
	ColorPicker backgroundColorPicker;
	@FXML
	Button saveButton, deleteButton;
	
	private Event selectedEvent;
	private List<Event> events;
	private Stage primaryStage;
	private final int TIME_INCREMENT = 5;
	
	public void start(Stage primaryStage, List<Event> events, Event e) {
		this.primaryStage = primaryStage;
		this.events = events;
		this.selectedEvent = e;
		LocalTime t = LocalTime.MIDNIGHT;
		//fill ComboBoxes with time choices for event
		do {
			String formattedTime = TimeUtilities.formatComboBoxTime(t);
			startTimeComboBox.getItems().add(formattedTime);
			endTimeComboBox.getItems().add(formattedTime);
			t = t.plusMinutes(TIME_INCREMENT);
		} while(!t.equals(LocalTime.MIDNIGHT));
		//set title
		titleTextField.setText(e.getTitle());
		//set date
		startDatePicker.setValue(e.getStartDateTime().toLocalDate());
		startDatePicker.setShowWeekNumbers(false);
		endDatePicker.setValue(e.getEndDateTime().toLocalDate());
		endDatePicker.setShowWeekNumbers(false);
		//set comboboxes to event starttime and endtime
		int startTimeIndex = (int)LocalTime.MIDNIGHT.until(e.getStartDateTime().toLocalTime(), ChronoUnit.MINUTES)/TIME_INCREMENT;
		int endTimeIndex = (int)LocalTime.MIDNIGHT.until(e.getEndDateTime().toLocalTime(), ChronoUnit.MINUTES)/TIME_INCREMENT;
		startTimeComboBox.getSelectionModel().select(startTimeIndex);
		endTimeComboBox.getSelectionModel().select(endTimeIndex);
		//set description
		descriptionTextArea.setText(e.getDescription());
		//set color
		backgroundColorPicker.setValue(e.getBackgroundColor());
	}
	@FXML
	public void saveEvent() {
		//System.out.println("save");
		//fields for event object
		String title = titleTextField.getText();
		String description = descriptionTextArea.getText();
		//time variables
		LocalTime startTime = LocalTime.MIDNIGHT.plusMinutes(startTimeComboBox.getSelectionModel().getSelectedIndex() * TIME_INCREMENT);
		LocalTime endTime = LocalTime.MIDNIGHT.plusMinutes(endTimeComboBox.getSelectionModel().getSelectedIndex() * TIME_INCREMENT);
		
		LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startTime);
		LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endTime);
		Color color = backgroundColorPicker.getValue();
		//check if title is empty
		if (title.isEmpty() || title == null) {
			title = "(No title)";
		}
		//make sure startdatetime is before enddatetime (cannot be equal either)
		if (!startDateTime.isBefore(endDateTime)) {
			showErrorPopup("Start must be before end of event.");
			return;
		}
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
				return;
			}else if (i == events.size()-1 && newEvent.startsBy(currentEvent)) { //after last event
				events.add(newEvent);
				primaryStage.close();
				return;
			}
			i++;
			currentEvent = events.get(i);
		}
		//if reached here, then newevent intersects with some event without any resolution
		showErrorPopup("This event would conflict with another event.");
	}
	
	@FXML
	public void deleteEvent() {
		//System.out.println("delete");
		events.remove(selectedEvent);
		primaryStage.close();
	}
	
	private void showErrorPopup(String message) {                
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.initOwner(primaryStage);
      alert.setTitle("Error");
      //didnt like header text
      alert.setHeaderText(null);
      alert.setGraphic(null);
      alert.setContentText(message);
      alert.showAndWait();
	}
}

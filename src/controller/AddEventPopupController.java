package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.CalendarModel;
import model.Event;

public class AddEventPopupController {
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
	Button saveButton;
	
	private CalendarModel cmodel;
	private ObservableList<Event> events;
	private Stage primaryStage;
	private int comboBoxWidth = 77;//specific, but perfectly fits time choices
	private final int TIME_INCREMENT = 5;
	
	public void start(Stage primaryStage, CalendarModel cmodel, ObservableList<Event> events) {
		this.primaryStage = primaryStage;
		this.cmodel = cmodel;
		this.events = events;
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
		//set default values
		titleTextField.setText("Event Title");
		startDatePicker.setValue(LocalDate.now());
		startDatePicker.setShowWeekNumbers(false);
		endDatePicker.setValue(LocalDate.now());
		endDatePicker.setShowWeekNumbers(false);
		startTimeComboBox.getSelectionModel().select(0);
		endTimeComboBox.getSelectionModel().select(0);
	}
	
	@FXML
	public void saveEvent() {
		//fields for event object
		String title = titleTextField.getText();
		String description = descriptionTextArea.getText();
		LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startTimeComboBox.getValue());
		LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endTimeComboBox.getValue());
		Color color = backgroundColorPicker.getValue();
		//check if title is empty
		if (title.isEmpty() || title == null) {return;}
		//make sure startdatetime is before enddatetime (cannot be equal either), does not allow events span days
		if (startDateTime.isAfter(endDateTime) || startDateTime.equals(endDateTime) ||
				!startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {return;}
		//create event object
		Event newEvent = new Event(title, description, startDateTime, endDateTime, color);
		//check where to place event and do no insert if it collides with other event(s)
		if (events.size() == 0) {//easy case: insert in empty list
			events.add(newEvent);
			cmodel.printEvents();
			primaryStage.close();
			return;
		}
		int i = 0;
		Event currentEvent = events.get(i);
		while(!newEvent.isDuring(currentEvent) && i<events.size()) {
			if (newEvent.isBefore(currentEvent)) {
				events.add(i,newEvent);
				cmodel.printEvents();//debugging
				primaryStage.close();
				break;
			}else if (i == events.size()-1 && newEvent.isAfter(currentEvent)) {//after last event
				events.add(newEvent);
				cmodel.printEvents();//debugging
				primaryStage.close();
				break;
			}
			i++;
			currentEvent = events.get(i);
		}
	}
}

package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Event;
import model.Timetable;

public class ViewEventPopupController {
	@FXML
	ScrollPane infoScrollPane;
	@FXML
	TextFlow eventInfo;
	@FXML
	Button modifyButton, removeButton;
	
	Stage primaryStage;
	Event e;
	List<Event> events;
	public void start(Stage primaryStage, List<Event> events, Event e) {
		this.primaryStage = primaryStage;
		this.e = e;
		this.events = events;
		Text titleText = new Text(e.getTitle() + "\n");
		titleText.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 25;");
		Text descriptionText = new Text(e.getDescription() + "\n");
		descriptionText.setStyle("-fx-font-size: 12;");
		//here we goooo
		Text timeframeText = new Text(getTimeframeText());
		timeframeText.setStyle("-fx-font-size: 12;");
		
		//add all text bois to textflow
		eventInfo.setPrefWidth(Timetable.WIDTH/2);//should be half of timetable width?
		eventInfo.setPrefHeight(Timetable.WIDTH/4);
		eventInfo.getChildren().addAll(titleText,descriptionText,timeframeText);
	}
	//remove previous event, add new one with modified fields
	private String getTimeframeText() {
		LocalDateTime startDateTime = e.getStartDateTime();
		LocalDate startDate = startDateTime.toLocalDate();
		LocalTime startTime = startDateTime.toLocalTime();
		LocalDateTime endDateTime = e.getEndDateTime();
		LocalDate endDate = endDateTime.toLocalDate();
		LocalTime endTime = endDateTime.toLocalTime();
		if (startDate.equals(endDate)) {//same day
			String dayOfWeek = startDate.getDayOfWeek().toString().toLowerCase();
			return dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1) 
					+ ", " + startDate + " " + formatTime(startTime) + "-" + formatTime(endTime);
		}else {//different days
			return startDate + ", " + formatTime(startTime) + "-" + endDate + ", " + formatTime(endTime);
		}
	}
	private String getPeriod(int militaryHour) {
		return militaryHour<12 ? "am" : "pm";
	}
	private int militaryToTwelveHour(int militaryHour) {
		return (militaryHour%12==0) ? 12 : militaryHour%12;//converts hour from military to 12-hour time
	}
	//time => hour:min am/pm
	private String formatTime(LocalTime t) {
		int militaryHour = t.getHour();
		int hour = militaryToTwelveHour(militaryHour);
		String period = getPeriod(militaryHour);
		String originalTime = t.toString();
		return hour + originalTime.substring(2) + period;
	}
	
	public void modifyEvent() {
		//create popup stage
		Stage popupStage = new Stage();
		//FXML stuff
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/modifyEventPopup.fxml"));
		try {
			//content of fxml file
			GridPane root = (GridPane)loader.load();
			//retrieve and start up controller
			ModifyEventPopupController modifyEventPopupController = loader.getController();
			modifyEventPopupController.start(popupStage, events, e);
			//set up stage
			popupStage.initModality(Modality.APPLICATION_MODAL);
			//modifyeventpopup's owner is same as current popup
			popupStage.initOwner(primaryStage.getOwner());
			popupStage.setScene(new Scene(root));
			popupStage.setTitle("Modify event");
			popupStage.setResizable(false);
			popupStage.centerOnScreen();
			popupStage.show();
			//close current window
			primaryStage.close();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	public void removeEvent() {
		events.remove(e);
		primaryStage.close();
	}
	
}

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
import model.TimeUtilities;
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
		Text timeframeText = new Text(TimeUtilities.formatDateTimeRange(e.getStartDateTime(), e.getEndDateTime()));
		timeframeText.setStyle("-fx-font-size: 12;");
		
		//add all text bois to textflow
		eventInfo.setPrefWidth(Timetable.WIDTH/2);//should be half of timetable width?
		eventInfo.setPrefHeight(Timetable.WIDTH/4);
		eventInfo.getChildren().addAll(titleText,descriptionText,timeframeText);
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

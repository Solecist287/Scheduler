package controller;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Event;

public class ViewEventPopupController {
	@FXML
	ScrollPane infoScrollPane;
	@FXML
	Label infoLabel;
	@FXML
	Button modifyButton, removeButton;
	
	Stage primaryStage;
	Event e;
	List<Event> events;
	public void start(Stage primaryStage, List<Event> events, Event e) {
		this.primaryStage = primaryStage;
		this.e = e;
		this.events = events;
	}
	//remove previous event, add new one with modified fields
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

package main;

import controller.MainDisplayController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.CalendarModel;

public class Scheduler extends Application{
	private CalendarModel cmodel;
	private MainDisplayController mainDisplayController;
	@Override
	public void start(Stage primaryStage) throws Exception {
		//read in events
		cmodel = CalendarModel.readData();
		//create fxml loader
		FXMLLoader loader = new FXMLLoader();
		//hook up loader with fxml file location
		loader.setLocation(getClass().getResource("/view/mainDisplay.fxml"));
		//retrieve fxml from location with loader
		BorderPane root = (BorderPane)loader.load();
		//retrieve and start up controller
		mainDisplayController = loader.getController();
		mainDisplayController.start(primaryStage, cmodel);
		//set up stage
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Scheduler");
		primaryStage.setResizable(true);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
	
	@Override
	public void stop() {
		//write stuff back to database
		CalendarModel.writeData(cmodel);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}

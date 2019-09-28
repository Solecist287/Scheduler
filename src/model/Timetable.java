package model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import controller.ViewEventPopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class Timetable {
	Stage mainStage;
	public List<Event> events;
	Node view;
	Locale locale;
	TemporalField dayOfWeekTemporalField;
	public static final int WIDTH = 840;//div by 4 and 7...ie 700, 728, 756, 784, 812, 840
	public static final int HEIGHT = 500;//arbitrary
	public static final int PADDING = 20;//for timetable display
	int rowNum, rowSize, colNum, colSize;
	LocalDate lastDateEntered;//highlighted date, if applicable
	
	public Timetable(Stage mainStage, List<Event> events, LocalDate initDate, Locale locale) {
		this.mainStage = mainStage;
		this.events = events;
		lastDateEntered = initDate;
		this.locale = locale;
		//set temporal field in order to know "beginning" of week like sunday,monday,etc.
		dayOfWeekTemporalField = WeekFields.of(locale).dayOfWeek();
		view = null;
	}
	public Node getView() {
		return view;
	}
	//view that represents entire timetable object
	public abstract void createView();
	//reconstructs visuals only if input date and lastdateentered are in different timeframes...not dynamic once in focus
	public abstract void update(LocalDate d);
	//adds nodes for an event if event is in same timeunit, dynamically changes timetable
	public abstract void onEventAdded(Event e);
	//removes nodes for an event if event is in same timeunit, dynamically changes timetable
	public abstract void onEventRemoved(Event e);
	//should add nodes of an event to list, timetable container, and add listeners
	public abstract void addVisuals(Event e, LocalDate d); 
	//clears all event related nodes
	public abstract void clearAllEventVisuals();
	//render all applicable event nodes (calls addNodes(e, date), if isRenderable(e,date)==true)
	public abstract void renderEventVisuals(LocalDate d);
	//called to see if event can be rendered to current state of view
	public abstract boolean isRenderable(Event e, LocalDate d);
	//called when event is clicked on
	public void viewEventPopup(Event e) {
		//create popup stage
		Stage popupStage = new Stage();
		//FXML stuff
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/viewEventPopup.fxml"));
		try {
			//content of fxml file
			GridPane root = (GridPane)loader.load();
			//retrieve and start up controller
			ViewEventPopupController viewEventPopupController = loader.getController();
			viewEventPopupController.start(popupStage, events, e);
			//set up stage
			popupStage.initModality(Modality.APPLICATION_MODAL);
			popupStage.initOwner(mainStage);
			popupStage.setScene(new Scene(root));
			popupStage.setTitle("View event");
			popupStage.setResizable(false);
			popupStage.centerOnScreen();
			popupStage.show();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	//displays text for timeunitcombobox
	public abstract String toString();
}

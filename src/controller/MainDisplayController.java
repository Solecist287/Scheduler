package controller;

import javafx.scene.control.ScrollPane;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CalendarModel;
import model.Event;

public class MainDisplayController {
	private CalendarModel cmodel;
	private List <Event> events;
	@FXML
	ScrollPane timetableDisplay;
	@FXML
	VBox calendarView;
	@FXML
	ComboBox<String> timeUnitComboBox;
	@FXML
	Label monthYearLabel;
	@FXML
	Button leftButton, rightButton;
	
	//all the timetable views
	GridPane dayTimetable, weekTimetable, monthTimetable, yearTimetable;
	
	DatePickerSkin datePickerDisplay;
	
	
	private Stage primaryStage;
	
	private DatePicker datePicker;
	private DatePickerSkin datePickerSkin;
	private Node datePickerSkinPopupContent;
	private TemporalField USDayOfWeekTemporalField;
	
	private final int timeIncrement = 5;
	//make divisible by 4 and 7 for months and week respectively
	//private final int timetableWidth = 560;
	private final int timetableWidth = 700;
	public void start(Stage primaryStage, CalendarModel cmodel) {
		this.primaryStage = primaryStage;
		//to get data for/from model
		this.cmodel = cmodel;
		//retrieve events list from database
		events = cmodel.getEvents();
		//create timetables like dimensions, cells, etc
		dayTimetable = createTimetable("Day");
		weekTimetable = createTimetable("Week");
		monthTimetable = createTimetable("Month");
		yearTimetable = createTimetable("Year");
		//set temporal field in order to know "beginning" of week like sunday,monday,etc.
		USDayOfWeekTemporalField = WeekFields.of(Locale.US).dayOfWeek();
		//get current date to initialize everything
		LocalDate currentDate = LocalDate.now();
		//create and initialize datepicker
		datePicker = new DatePicker(currentDate);
		datePicker.valueProperty().addListener((selected,oldval,newval)->{
			//change label based on combobox value
			updateMonthYearLabel();
			updateTimetable();
		});
		//extract skin from datepicker for calendar display
		datePickerSkin = new DatePickerSkin(datePicker);
		datePickerSkinPopupContent = datePickerSkin.getPopupContent();
		datePickerSkinPopupContent.setStyle("-fx-border-color: black;");
		//add calendar display to left side of screen
		calendarView.getChildren().add(0,datePickerSkinPopupContent);
		//add listener to timeUnitComboBox
		timeUnitComboBox.getSelectionModel().selectedItemProperty().addListener((selected,oldval,newval)->{
			//add hover text for left and right arrow buttons
			leftButton.setTooltip(new Tooltip("Previous " + timeUnitComboBox.getSelectionModel().getSelectedItem()));
			rightButton.setTooltip(new Tooltip("Next " + timeUnitComboBox.getSelectionModel().getSelectedItem()));
			updateMonthYearLabel();
			updateTimetable();
		});
		//initialize timeunitcombobox to time unit "Day" (triggers its listener to init stuff)
		timeUnitComboBox.getSelectionModel().select(0);
	}
	public void stop() {}
	
	@FXML
	public void addEventPopup() throws IOException {
		//create popup stage
		Stage popupStage = new Stage();
		//FXML stuff
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/addEventPopup.fxml"));
		//content of fxml file
		GridPane root = (GridPane)loader.load();
		//retrieve and start up controller
		AddEventPopupController addEventPopupController = loader.getController();
		addEventPopupController.start(popupStage, primaryStage, cmodel);
		//set up stage
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.initOwner(primaryStage);
		popupStage.setScene(new Scene(root));
		popupStage.setTitle("Add an event");
		popupStage.setResizable(false);
		popupStage.centerOnScreen();
		popupStage.show();
	}
	
	@FXML
	public void setToToday() {
		//get today's date
		LocalDate currentDate = LocalDate.now();
		//change calendarview which changes label
		datePicker.setValue(currentDate);
		//change scheduleview
	}
	
	@FXML
	public void setToPrevTimeUnit() {
		//update calendar, label, schedule...label is affected when calendar or dropdown is changed 
		String currentTimeUnit = timeUnitComboBox.getSelectionModel().getSelectedItem();
		LocalDate currentDate = datePicker.getValue();
		switch(currentTimeUnit) {
			case "Day":
				datePicker.setValue(currentDate.minusDays(1));
				break;
			case "Week":
				datePicker.setValue(currentDate.minusWeeks(1));
				break;
			case "Month":
				datePicker.setValue(currentDate.minusMonths(1));
				break;
			case "Year":
				datePicker.setValue(currentDate.minusYears(1));
				break;
		}
	}
	
	@FXML
	public void setToNextTimeUnit() {
		//update calendar, label, schedule...label is affected when calendar or dropdown is changed 
		String currentTimeUnit = timeUnitComboBox.getSelectionModel().getSelectedItem();
		LocalDate currentDate = datePicker.getValue();
		switch(currentTimeUnit) {
			case "Day":
				datePicker.setValue(currentDate.plusDays(1));
				break;
			case "Week":
				datePicker.setValue(currentDate.plusWeeks(1));
				break;
			case "Month":
				datePicker.setValue(currentDate.plusMonths(1));
				break;
			case "Year":
				datePicker.setValue(currentDate.plusYears(1));
				break;
		}
	}
	
	private void updateTimetable() {
		String currentTimeUnit = timeUnitComboBox.getSelectionModel().getSelectedItem();
		GridPane timetable = null;
		switch(currentTimeUnit) {
		case "Day":
			timetableDisplay.setContent(dayTimetable);
			timetable = dayTimetable;
			break;
		case "Week":
			timetableDisplay.setContent(weekTimetable);
			timetable = weekTimetable;
			break;
		case "Month":
			timetableDisplay.setContent(monthTimetable);
			timetable = monthTimetable;
			break;
		case "Year":
			timetableDisplay.setContent(yearTimetable);
			timetable = yearTimetable;
			break;
		}
		
		//find a way to clear events...maybe recreate table...?
	}
	
	private GridPane createTimetable(String timeUnit) {
		//init to zero first
		int rowSize = 0; 
		int colSize = 0;
		int rowNum = 0;
		int colNum = 0;
		GridPane timetable = new GridPane();
		//determine dimensions of grid
		switch(timeUnit) {
		case "Day":
			rowNum = (24*60)/timeIncrement;//5 min intervals
			colNum = 1;//one day to show
			rowSize = 5;
			colSize = timetableWidth;
			break;
		case "Week":
			rowNum = (24*60)/timeIncrement;//5 min intervals
			colNum = 7;//7 days a week, one day per column
			rowSize = 5;
			colSize = (timetableWidth)/7;//divide into 7ths..."minus 6" keeps same size timetable when using hgap for cols
			break;
		case "Month"://not sure yet
			break;
		case "Year"://not sure yet
			break;
		}
		//set column and row constraints
		for (int i = 0; i < colNum; i++) {
			ColumnConstraints cc = new ColumnConstraints(colSize);
			cc.setHgrow(Priority.NEVER);
			timetable.getColumnConstraints().add(cc);
		}
		for (int i = 0; i < rowNum; i++) {
			RowConstraints rc = new RowConstraints(rowSize);
			timetable.getRowConstraints().add(rc);
		}
		//draw timetable
		for (int i = 0; i < colNum; i++) {
			for (int j = 0; j < rowNum; j++) {
				Pane p = new Pane();
				if (j%12==0) {//start of hour, show only top of cell
					p.setStyle("-fx-background-color: white;" +
							"-fx-border-width: 1 1 0 1;" +
							"-fx-border-color: black black black black");
				}else if (j == rowNum - 1){//last row of cell
					p.setStyle("-fx-background-color: white;" +
							"-fx-border-width: 0 1 1 1;" +
							"-fx-border-color: black black black black");
				}else {//just draw left and right lines of cell
					p.setStyle("-fx-background-color: white;" +
							"-fx-border-width: 0 1 0 1;" +
							"-fx-border-color: black black black black");
				}
				timetable.add(p, i, j, 1, 1);
			}
		}
		Pane p = new Pane();
		p.setStyle("-fx-background-color: red");
		Pane p2 = new Pane();
		p2.setStyle("-fx-background-color: red");
		//timetable.add(p, 0, 0, 1, 36);
		timetable.add(p2, 1, 10, 1, 1);
		//timetable.setGridLinesVisible(true);
		timetable.getChildren().remove(p);
		//timetable.getChildren().remove(p2);
		return timetable;
	}
	
	private void updateMonthYearLabel() {
		LocalDate currentDate = datePicker.getValue();
		String currentTimeUnit = timeUnitComboBox.getSelectionModel().getSelectedItem();
		Month currentMonth = currentDate.getMonth();
		String currentMonthStr = currentMonth.toString();
		//first three letters of month. only first letter capitalized
		String currentMonthAbbrev = currentMonthStr.substring(0,1) + currentMonthStr.substring(1,3).toLowerCase();
		int currentYear = currentDate.getYear();
		switch(currentTimeUnit) {
			case "Week"://"MONTH YEAR" or "MONTH-MONTH YEAR"
				Month beginningOfCurrentWeekMonth = currentDate.with(USDayOfWeekTemporalField, 1).getMonth();
				String beginningOfCurrentWeekMonthStr = beginningOfCurrentWeekMonth.toString();
				String beginningOfCurrentWeekMonthAbbrev = beginningOfCurrentWeekMonthStr.substring(0,1)
						+ beginningOfCurrentWeekMonthStr.substring(1,3).toLowerCase();
				Month endOfCurrentWeekMonth = currentDate.with(USDayOfWeekTemporalField,7).getMonth();
				String endOfCurrentWeekMonthStr = endOfCurrentWeekMonth.toString();
				String endOfCurrentWeekMonthAbbrev = endOfCurrentWeekMonthStr.substring(0,1) +
						endOfCurrentWeekMonthStr.substring(1,3).toLowerCase();
				if (!beginningOfCurrentWeekMonthAbbrev.equals(currentMonthAbbrev)) {
					monthYearLabel.setText(beginningOfCurrentWeekMonthAbbrev + "-" + currentMonthAbbrev + " " + currentYear);
				}else if (!endOfCurrentWeekMonthAbbrev.equals(currentMonthAbbrev)) {
					monthYearLabel.setText(currentMonthAbbrev + "-" + endOfCurrentWeekMonthAbbrev + " " + currentYear);
				}else {
					monthYearLabel.setText(currentMonthAbbrev + " " + currentYear);
				}
				break;
			case "Year"://just shows "YEAR"
				monthYearLabel.setText(currentYear + "");
				break;
			default://MONTH and DAY both show "MONTH YEAR"
				//month and day are the same case
				monthYearLabel.setText(currentMonthAbbrev + " " + currentYear);
				break;
		}
	}
	
}

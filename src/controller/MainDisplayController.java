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
import javafx.geometry.Pos;
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
	BorderPane mainDisplay;
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
	
	private Stage primaryStage;
	
	private DatePicker datePicker;
	private DatePickerSkin datePickerSkin;
	private Node datePickerSkinPopupContent;
	private DatePickerSkin datePickerDisplay;//pointless?
	
	private TemporalField USDayOfWeekTemporalField;//later be a general setting
	
	//abstract current timetable var
	private final int timeIncrement = 5;
	private final int timeLabelWidth = 50;//was 50
	private final int timetableDisplayPadding = 20;
	//make divisible by 4 and 7 for months and week respectively
	private final int timetableWidth = 700;
	public void start(Stage primaryStage, CalendarModel cmodel) {
		this.primaryStage = primaryStage;
		//to get data for/from model
		this.cmodel = cmodel;
		//retrieve events list from database
		events = cmodel.getEvents();
		//timetableDisplay width
		timetableDisplay.setMinWidth(timetableWidth + timeLabelWidth + timetableDisplayPadding);//later use getters of current grid
		//set temporal field in order to know "beginning" of week like sunday,monday,etc.
		USDayOfWeekTemporalField = WeekFields.of(Locale.US).dayOfWeek();
		//get current date to initialize everything
		LocalDate today = LocalDate.now();
		//create and initialize datepicker
		datePicker = new DatePicker(today);
		datePicker.valueProperty().addListener((selected,oldval,newval)->{
			//change label based on combobox value
			System.out.println("datepicker listener called");
			updateMonthYearLabel();
			updateTimetable();
		});
		//extract skin from datepicker for calendar display
		datePickerSkin = new DatePickerSkin(datePicker);//pointless to assign?
		datePickerSkinPopupContent = datePickerSkin.getPopupContent();
		datePickerSkinPopupContent.setStyle("-fx-border-color: black;");
		//add calendar display to left side of screen
		calendarView.getChildren().add(0,datePickerSkinPopupContent);
		//create timetables like dimensions, cells, etc
		dayTimetable = createDayTimetable(1);
		weekTimetable = createDayTimetable(7);
		monthTimetable = null;
		yearTimetable = null;
		//add listener to timeUnitComboBox
		timeUnitComboBox.getSelectionModel().selectedItemProperty().addListener((selected,oldval,newval)->{
			System.out.println("timeUnitComboBox listener called");
			//add hover text for left and right arrow buttons
			leftButton.setTooltip(new Tooltip("Previous " + newval));
			rightButton.setTooltip(new Tooltip("Next " + newval));
			updateMonthYearLabel();
			updateTimetable();
		});
		//initialize timeunitcombobox to time unit "Day" (triggers its listener to init stuff)
		timeUnitComboBox.getSelectionModel().select(0);
	}
	public void stop() {}
	//observable list would handle updating the timetable current view
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
		updateTimetable();
	}
	
	//done?
	@FXML
	public void setToPrevTimeUnit() {
		//update calendar, label, schedule...label is affected when calendar or dropdown is changed 
		String currentTimeUnit = getSelectedTimeUnit();
		LocalDate currentDate = getSelectedDate();
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
	//done?
	@FXML
	public void setToNextTimeUnit() {
		//update calendar, label, schedule...label is affected when calendar or dropdown is changed 
		String currentTimeUnit = getSelectedTimeUnit();
		LocalDate currentDate = getSelectedDate();
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
	
	//hard?
	private void updateTimetable() {
		String currentTimeUnit = getSelectedTimeUnit();//paramter instead?
		switch(currentTimeUnit) {
		case "Day":
			timetableDisplay.setContent(dayTimetable);
			break;
		case "Week":
			timetableDisplay.setContent(weekTimetable);
			break;
		case "Month":
			timetableDisplay.setContent(monthTimetable);
			break;
		case "Year":
			timetableDisplay.setContent(yearTimetable);
			break;
		}
		//find a way to clear events...maybe recreate table...?
	}
	
	private GridPane createDayTimetable(int dayNum) {
		//init to zero first
		int rowSize = timeIncrement; 
		int colSize = timetableWidth/dayNum;
		int rowNum = (24*60)/timeIncrement;//5 min intervals;
		int colNum = dayNum + 1;//first is time label column
		GridPane timetable = new GridPane();
		//set column and row constraints
		for (int i = 0; i < colNum; i++) {
			ColumnConstraints cc;
			if (i == 0) {//time label
				cc = new ColumnConstraints(timeLabelWidth);
			}else {
				cc = new ColumnConstraints(colSize);
			}
			cc.setHgrow(Priority.NEVER);
			timetable.getColumnConstraints().add(cc);
		}
		for (int i = 0; i < rowNum; i++) {
			RowConstraints rc = new RowConstraints(rowSize);
			rc.setVgrow(Priority.NEVER);
			timetable.getRowConstraints().add(rc);
		}
		LocalDate currentDate = getSelectedDate();
		for (int i = 1; i <= 7; i++) {
			LocalDate localDate = currentDate.with(USDayOfWeekTemporalField,i);
			String day = localDate.getDayOfWeek().toString().substring(0,3).toLowerCase();
			System.out.println("date: " + localDate + " day: " + day + " i: " + i);
		}
		//draw and set initial values for timetable
		for (int i = 0; i < colNum; i++) {
			for (int j = 0; j < rowNum; j++) {
				Pane p = new Pane();
				p.setPrefWidth(colSize);
				p.setPrefHeight(rowSize);
				if (i==0) {//column for time labels. each label is 1 col x 2 rows 
					if (j%12==0){//show only top border otherwise
						String period = (j/12)<12 ? "am" : "pm";
						int hour = ((j/12)%12==0) ? 12 : (j/12)%12;//do NOT change!!! 
						System.out.println(j + ": " + hour + " " + period);
						Label timeLabel = new Label(hour + period);
						timeLabel.setMinHeight(rowSize*2);
						timeLabel.setMinWidth(colSize);
						timeLabel.setStyle("-fx-background-color: white;" + 
								"-fx-border-width: 1 0 0 1;" +
								"-fx-border-color: black black black black");
						timetable.add(timeLabel, i, j, 1, 3);
						j+=2;
					}else {//draw bottom line and left of other cells 
						if (j == rowNum-1) {//bottom
							p.setStyle("-fx-background-color: white;" +
									"-fx-border-width: 0 0 1 1;" +
									"-fx-border-color: black black black black");
						}else {//rest
							p.setStyle("-fx-background-color: white;" +
									"-fx-border-width: 0 0 0 1;" +
									"-fx-border-color: black black black black");
						}
						timetable.add(p, i, j);
					}
				}else if (j%12==0) {//start of hour, show only top of cell
					if (i==colNum-1) {
						p.setStyle("-fx-background-color: white;" +
								"-fx-border-width: 1 1 0 1;" +
								"-fx-border-color: black black black black");
					}else {
						p.setStyle("-fx-background-color: white;" +
								"-fx-border-width: 1 0 0 1;" +
								"-fx-border-color: black black black black");
					}
					timetable.add(p, i, j);
				}else if (j == rowNum - 1){//last row of cell
					if (i==colNum-1) {
						p.setStyle("-fx-background-color: white;" +
								"-fx-border-width: 0 1 1 1;" +
								"-fx-border-color: black black black black");
					}else {
						p.setStyle("-fx-background-color: white;" +
								"-fx-border-width: 0 0 1 1;" +
								"-fx-border-color: black black black black");
					}
					
					timetable.add(p, i, j);
				}else {//just draw left and right lines of cell
					if (i==colNum-1) {
						p.setStyle("-fx-background-color: white;" +
								"-fx-border-width: 0 1 0 1;" +
								"-fx-border-color: black black black black");
					}else {
						p.setStyle("-fx-background-color: white;" +
								"-fx-border-width: 0 0 0 1;" +
								"-fx-border-color: black black black black");
					}
					timetable.add(p, i, j);
				}
			}
		}
		
		Pane p = new Pane();
		p.setStyle("-fx-background-color: red");
		//Pane p2 = new Pane();
		//p2.setStyle("-fx-background-color: red");
		//timetable.add(p, 0, 280, 1, 8);
		//timetable.add(p2, 6, 10, 1, 1);
		//timetable.getChildren().remove(p);
		//timetable.getChildren().remove(p2);
		
		//timetable.setGridLinesVisible(true);
		return timetable;
	}
	private void updateMonthYearLabel() {
		LocalDate currentDate = getSelectedDate();
		String currentTimeUnit = getSelectedTimeUnit();
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
	private LocalDate getSelectedDate() {
		return datePicker.getValue();
	}
	private String getSelectedTimeUnit() {
		return timeUnitComboBox.getSelectionModel().getSelectedItem();
	}
}

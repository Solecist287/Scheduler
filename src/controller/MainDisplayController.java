package controller;

import javafx.scene.control.ScrollPane;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CalendarModel;
import model.DayTimetable;
import model.Event;
import model.MonthTimetable;
import model.Timetable;
import model.WeekTimetable;
import model.YearTimetable;

public class MainDisplayController {
	@FXML
	BorderPane timetableDisplay;
	@FXML
	VBox calendarView;
	@FXML
	ComboBox<Timetable> timeUnitComboBox;
	@FXML
	Label monthYearLabel;
	@FXML
	Button leftButton, rightButton;
	//all the timetable views
	//may not need to save day/week/month/yeartimetables to vars
	private Timetable dayTimetable, weekTimetable, monthTimetable, yearTimetable, selectedTimetable;
	
	private CalendarModel cmodel;
	private ObservableList<Event> events; 
	
	private Stage primaryStage;
	
	private DatePicker datePicker;//underlying date selection logic
	private Node datePickerSkinPopupContent;//display for date selection
	
	private TemporalField dayOfWeekTemporalField;//later be a general setting
	private Locale localeSetting;
	
	public void start(Stage primaryStage, CalendarModel cmodel) {
		this.primaryStage = primaryStage;
		this.cmodel = cmodel;
		//retrieve events and set listener to update selectedtimetable
		events = cmodel.getEvents();
		events.addListener((ListChangeListener<Event>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                	for (Event e : change.getAddedSubList()) {
                		dayTimetable.onEventAdded(e);
                		weekTimetable.onEventAdded(e);
                		//monthTimetable.addEvent(e);
                		//yearTimetable.addEvent(e);
                	}
                } else if (change.wasRemoved()) {
                    for (Event e : change.getRemoved()) {
                    	dayTimetable.onEventRemoved(e);
                    	weekTimetable.onEventRemoved(e);
                    	//monthTimetable.removeEvent(e);
                    	//yearTimetable.removeEvent(e);
                    }
                }
            }
        });
		//timetableDisplay width
		//timetableDisplay.setMinWidth(Timetable.WIDTH + PADDING);//later use getters of current grid
		//default locale
		localeSetting = Locale.US;
		//set temporal field in order to know "beginning" of week like sunday,monday,etc.
		dayOfWeekTemporalField = WeekFields.of(localeSetting).dayOfWeek();
		//set locale so calendar is appropriate for region
		Locale.setDefault(localeSetting);
		//get current date to initialize everything
		LocalDate today = LocalDate.now();
		//create and initialize datepicker
		datePicker = new DatePicker(today);
		datePicker.setShowWeekNumbers(false);
		datePicker.valueProperty().addListener((selected,oldval,newval)->{
			//System.out.println("datepicker listener called");
			updateMonthYearLabel();
			//update timetable
			selectedTimetable.update(newval);
		});
		//extract visuals from datepicker
		datePickerSkinPopupContent = new DatePickerSkin(datePicker).getPopupContent();
		datePickerSkinPopupContent.setStyle("-fx-border-color: black;");
		//add calendar display to left side of screen
		calendarView.getChildren().add(0,datePickerSkinPopupContent);
		//create timetables
		dayTimetable = new DayTimetable(primaryStage, events, today, dayOfWeekTemporalField);
		weekTimetable = new WeekTimetable(primaryStage, events, today, dayOfWeekTemporalField);
		monthTimetable = new MonthTimetable(primaryStage, events, today, dayOfWeekTemporalField);
		yearTimetable = new YearTimetable(primaryStage, events, today, dayOfWeekTemporalField);
		//populate combobox with timetables
		timeUnitComboBox.getItems().addAll(dayTimetable, weekTimetable, monthTimetable, yearTimetable);
		//add listener to timeUnitComboBox
		timeUnitComboBox.getSelectionModel().selectedItemProperty().addListener((selected,oldval,newval)->{
			//System.out.println("timeUnitComboBox listener called");
			//add hover text for left and right arrow buttons
			leftButton.setTooltip(new Tooltip("Previous " + newval));
			rightButton.setTooltip(new Tooltip("Next " + newval));
			updateMonthYearLabel();
			//change timetable display
			selectedTimetable = newval;//set selected timetable
			selectedTimetable.update(getSelectedDate());//update timetable itself
			timetableDisplay.setCenter(selectedTimetable.getView());//show updated timetable
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
		try {
		//content of fxml file
		GridPane root = (GridPane)loader.load();
		//retrieve and start up controller
		AddEventPopupController addEventPopupController = loader.getController();
		addEventPopupController.start(popupStage, cmodel, events, datePicker.getValue());
		//set up stage
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.initOwner(primaryStage);
		popupStage.setScene(new Scene(root));
		popupStage.setTitle("Add event");
		popupStage.setResizable(false);
		popupStage.centerOnScreen();
		popupStage.show();
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	@FXML
	public void setToToday() {
		//get today's date
		LocalDate currentDate = LocalDate.now();
		//change calendarview which changes label
		datePicker.setValue(currentDate);
		//after date is changed, update current timetable
		selectedTimetable.update(getSelectedDate());
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
		//after date is changed, update current timetable
		selectedTimetable.update(getSelectedDate());
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
		//after date is changed, update current timetable
		selectedTimetable.update(getSelectedDate());
	}
	
	private void updateMonthYearLabel() {
		LocalDate date = getSelectedDate();
		String timeUnit = getSelectedTimeUnit();
		int year = date.getYear();
		switch(timeUnit) {
			case "Week"://"MONTH YEAR" or "MONTH-MONTH YEAR" depending on if week is between two months
				String startMonth = date.with(dayOfWeekTemporalField, 1).getMonth().toString();
				String startMonthAbbrev = startMonth.substring(0,1) + startMonth.substring(1,3).toLowerCase();
				String endMonth = date.with(dayOfWeekTemporalField,7).getMonth().toString();
				String endMonthAbbrev = endMonth.substring(0,1) + endMonth.substring(1,3).toLowerCase();
				if (!startMonthAbbrev.equals(endMonthAbbrev)) {
					monthYearLabel.setText(startMonthAbbrev + "-" + endMonthAbbrev + " " + year);
				}else {//otherwise arbitrarily choose one of them since both are the same
					monthYearLabel.setText(startMonthAbbrev + " " + year);
				}
				break;
			case "Year"://just shows "YEAR"
				monthYearLabel.setText(year + "");
				break;
			default://MONTH and DAY both show "MONTH YEAR"
				String month = date.getMonth().toString();
				String monthAbbrev = month.substring(0,1) + month.substring(1,3).toLowerCase();//"Jun", "Feb", etc.
				monthYearLabel.setText(monthAbbrev + " " + year);
				break;
		}
	}
	private LocalDate getSelectedDate() {
		return datePicker.getValue();
	}
	private String getSelectedTimeUnit() {
		return timeUnitComboBox.getSelectionModel().getSelectedItem().toString();
	}
}

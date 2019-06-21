package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class YearTimetable extends Timetable {
	ScrollPane yearGridContainer;
	GridPane yearGrid;
	static final int MONTH_ROW_NUM = 7;
	static final int MONTH_COL_NUM = 7;
	static final int MONTH_NUM = 12;
	static final int MONTH_PADDING = 14;
	static final int CONTAINER_PADDING = 10;
	static final int MONTH_DAYS_NUM = 42;
	static final int TOTAL_DAYS = MONTH_DAYS_NUM * MONTH_NUM;
	List<Dayslot> dayslots;
	public YearTimetable(Stage mainStage, List<Event> events, LocalDate initDate, Locale locale) {
		super(mainStage, events, initDate, locale);
		//set timetable dimensions
		rowNum = 4;
		colNum = 3;
		colSize = (WIDTH - MONTH_PADDING * (colNum + 1))/colNum;
		rowSize = colSize;
		//create lists
		dayslots = new ArrayList<Dayslot>();
		
		//create view
		createView();
		updateHeaderLabels(initDate);
		renderEventVisuals(initDate);
	}

	@Override
	public void createView() {
		yearGrid = new GridPane();
		yearGrid.setStyle("-fx-background-color: white;");
		yearGrid.setHgap(MONTH_PADDING);
		yearGrid.setVgap(MONTH_PADDING);
		yearGrid.setPadding(new Insets(MONTH_PADDING,MONTH_PADDING,MONTH_PADDING,MONTH_PADDING));
		//draw monthly calendars
		for (int j = 0; j < rowNum; j++) {
			for (int i = 0; i < colNum; i++) {
				int monthIndex = colNum * j + i + 1;//starts at 1 for january
				String monthName = Month.of(monthIndex).toString().toLowerCase();
				String monthNameCapitalized = monthName.substring(0,1).toUpperCase() + monthName.substring(1);
				//labels for months' names
				Label monthNameHeader = new Label(monthNameCapitalized);
				monthNameHeader.setStyle("-fx-background-color: white;");
				int monthGridCellLength = colSize/7;
				//grid for current month
				GridPane monthGrid = new GridPane();
				for (int k = 0; k < MONTH_ROW_NUM; k++) {
					for (int l = 0; l < MONTH_COL_NUM; l++) {
						//add nodes to correct positions in monthgrid
						if (k == 0) {//if first row, set days of week symbols
							//set style and other parameters for label
							String style = "-fx-background-color: white;";
							Label dayLabel = new Label();
							dayLabel.setText(lastDateEntered.with(dayOfWeekTemporalField,l+1)
									.getDayOfWeek().toString()
									.substring(0,1));
							dayLabel.setStyle(style);
							dayLabel.setAlignment(Pos.CENTER);
							dayLabel.setPrefSize(monthGridCellLength, monthGridCellLength);
							//add to grid
							monthGrid.add(dayLabel, l, k);
						}else {
							//set on click listener based on offset and lastdatentered
							Dayslot dayslot = new Dayslot(dayslots.size(), monthGridCellLength);
							//add to dayslots
							dayslots.add(dayslot);
							//add to grid
							monthGrid.add(dayslot.getButton(), l, k);
						}
					}
				}
				//add header and monthgrid to container
				VBox container = new VBox();//will not be used for first column since we use hour labels
				container.getChildren().addAll(monthNameHeader, monthGrid);
				container.setAlignment(Pos.CENTER);
				//add container to grid
				yearGrid.add(container, i, j);
			}
		}
		yearGridContainer = new ScrollPane();
		yearGridContainer.setPrefHeight(HEIGHT);
		yearGridContainer.setPrefWidth(WIDTH + PADDING);
		yearGridContainer.setContent(yearGrid);
		view = yearGridContainer;
	}

	@Override
	public void update(LocalDate d) {
		//only do render new year timeframe if date is in another year timeframe than lastDateEntered
		if (d.getYear()!=lastDateEntered.getYear()) {
			//update day labels
			updateHeaderLabels(d);
			//restore dayslots to default values
			clearAllEventVisuals();
			//render events
			renderEventVisuals(d);
		}
		lastDateEntered = d;
	}

	@Override
	public String toString() {
		return "Year";
	}

	@Override
	public void onEventAdded(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			addVisuals(e, lastDateEntered);
		}
	}

	@Override
	public void onEventRemoved(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			for (int i = 0; i < dayslots.size(); i++) {
				Dayslot dayslot = dayslots.get(i);
				if (isRenderableForDayslot(e,dayslot.getDate(lastDateEntered))) {
					dayslot.removeEvent(e);
				}
			}
		}
	}

	@Override
	public void renderEventVisuals(LocalDate d) {
		LocalDate endOfTimeframe = d.withMonth(12).withDayOfMonth(1).plusDays(MONTH_DAYS_NUM-1);
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			//stop searching once past the end of the year
			if (e.getStartDateTime().toLocalDate().isAfter(endOfTimeframe)) {
				break;//stop searching
			//add dayslot if falls in the current year or intersects year
			}else if (isRenderable(e,d)) {
				addVisuals(e, d);
			}
		}
	}
	public boolean isRenderableForDayslot(Event e, LocalDate d) {
		LocalDateTime start = e.getStartDateTime();
		LocalDateTime end = e.getEndDateTime();
		LocalDate tomorrow = d.plusDays(1);
		LocalDate yesterday = d.minusDays(1);
		return start.toLocalDate().isBefore(tomorrow) && end.toLocalDate().isAfter(yesterday) 
				&& !end.equals(LocalDateTime.of(d, LocalTime.MIDNIGHT));
	}
	@Override
	public boolean isRenderable(Event e, LocalDate d) {
		LocalDateTime start = e.getStartDateTime();
		LocalDateTime end = e.getEndDateTime();
		LocalDate beforeTimeframe = d.withMonth(1).withDayOfMonth(1).with(dayOfWeekTemporalField,1).minusDays(1);
		LocalDate afterTimeframe = d.withMonth(12).withDayOfMonth(1).plusDays(MONTH_DAYS_NUM);
		return start.toLocalDate().isBefore(afterTimeframe) && end.toLocalDate().isAfter(beforeTimeframe)
				&& !end.equals(LocalDateTime.of(beforeTimeframe.plusDays(1), LocalTime.MIDNIGHT));
	}

	@Override
	public void clearAllEventVisuals() {
		for (int i = 0; i < dayslots.size(); i++) {
			dayslots.get(i).clear();
		}
	}

	@Override
	public void addVisuals(Event e, LocalDate d) {
		for (int i = 0; i < dayslots.size(); i++) {
			Dayslot dayslot = dayslots.get(i);
			if (isRenderableForDayslot(e,dayslot.getDate(d))) {
				dayslot.addEvent(e);
			}
		}
	}
	
	private void updateHeaderLabels(LocalDate d) {
		for (int i = 0; i < dayslots.size(); i++) {//for each month...
			Dayslot currentDayslot = dayslots.get(i);
			LocalDate dayslotDate = getDateFromIndex(i,d); 
			currentDayslot.setText(dayslotDate.getDayOfMonth() + "");
		}
	}
	//calculates date of dayslot based on its index in the list of dayslots
	private LocalDate getDateFromIndex(int index, LocalDate d) {
		int buttonOffset = index%MONTH_DAYS_NUM;
		int monthValue = index/MONTH_DAYS_NUM + 1;
		return d.withMonth(monthValue)
				.withDayOfMonth(1)
				.with(dayOfWeekTemporalField,1)
				.plusDays(buttonOffset);
	}

	//onclick handler for dayslots
	public void viewDayEventsPopup(LocalDate d) {
		int width = Timetable.WIDTH/2;//should be half of timetable width?
		int height = Timetable.WIDTH/4;
		//create popup stage
		Stage popupStage = new Stage();
		Label header = new Label(TimeUtilities.formatDate(d));
		header.setStyle("-fx-background-color: white;");
		//set events that intersect with date
		ObservableList<Event> dayEventsList = FXCollections.observableArrayList();
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			if (isRenderableForDayslot(e,d)) {
				dayEventsList.add(e);
			}
		}
		//create container for header and listview
		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10,10,10,10));
		popupContent.getChildren().addAll(header);
		popupContent.setAlignment(Pos.CENTER);
		popupContent.setStyle("-fx-background-color: white;");
		//create and add listview to container if there's any items in list
		if (dayEventsList.size()>0) {
			ListView<Event> dayEventsListView = new ListView<Event>();
			dayEventsListView.setPrefWidth(width);
			dayEventsListView.setPrefHeight(height);
			dayEventsListView.setCellFactory(x -> {
				return new ListCell<Event>() {
					@Override
					protected void updateItem(Event item, boolean empty) {
						super.updateItem(item,empty);
						if (item == null) {
			                setGraphic(null);
			                setText(null);
			                setStyle(null);
			                setOnMouseClicked(null);
			                updateSelected(false);
						}else {
							Color c = item.getBackgroundColor();
							String formattedTime = TimeUtilities.formatTime(item.getStartDateTime().toLocalTime());
							String text = item.getTitle() + " " + formattedTime;
							setGraphic(null);
							setText(text);
							setPrefWidth(width - PADDING);
							setStyle("-fx-background-color: rgb(" + c.getRed()*255 + "," + c.getGreen()*255 + "," + c.getBlue()*255 + ");"
									+ "-fx-border-color: black;" + "-fx-font-size: 12");
							setOnMouseClicked(event -> {
								viewEventPopup(item);
								updateSelected(false);
								popupStage.close();
							});
							updateSelected(false);
						}
					}
				};
			});
			dayEventsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			dayEventsListView.setItems(dayEventsList);
			//add to container
			popupContent.getChildren().add(dayEventsListView);
		}else {
			Label message = new Label("No events on this day");
			message.setPrefWidth(width);
			message.setPrefHeight(height);
			message.setAlignment(Pos.CENTER);
			//add to container
			popupContent.getChildren().add(message);
		}
		//set up stage
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.initOwner(mainStage);
		popupStage.setScene(new Scene(popupContent));
		popupStage.setTitle("View day's events");
		popupStage.setResizable(false);
		popupStage.centerOnScreen();
		popupStage.show();
	}
	public class Dayslot{
		private String defaultStyle = "-fx-background-color:white;";
		private String boldStyle = "-fx-font-weight: bold;";
		private Button dayButton;
		private List<Event> dayEvents;
		private int index;
		public Dayslot(int index, int buttonLength) {
			this.index = index;
			dayButton = new Button();
			dayButton.setAlignment(Pos.CENTER);
			dayButton.setStyle(defaultStyle);
			dayButton.setPrefSize(buttonLength, buttonLength);
			dayButton.setOnAction(e->{
				//retrieve date clicked
				LocalDate d = getDate(lastDateEntered);
				viewDayEventsPopup(d);
			});
			dayEvents = new ArrayList<Event>();
		}
		public void addEvent(Event e) {
			dayEvents.add(e);
			updateStyle();
		}
		public void removeEvent(Event e) {
			dayEvents.remove(e);
			updateStyle();
		}
		public void updateStyle() {
			if (dayEvents.size() == 0) {
				dayButton.setStyle(defaultStyle);
				return;
			}
			//blur colors (by averaging) of events on that day
			double r = 0;
			double g = 0;
			double b = 0;
			double weight = 1.0/dayEvents.size();
			for (int i = 0; i < dayEvents.size(); i++) {
				Color c = dayEvents.get(i).getBackgroundColor();
				r += c.getRed() * weight;
				g += c.getGreen() * weight;
				b += c.getBlue() * weight;
			}
			dayButton.setStyle(boldStyle + "-fx-background-color: rgb(" + r*255 + "," + g*255 + "," + b*255 + ");");
		}
		public LocalDate getDate(LocalDate d) {
			return getDateFromIndex(index,d);
		}
		public void setText(String text) {
			dayButton.setText(text);
		}
		public void clear() {
			//set default style for button
			dayButton.setStyle(defaultStyle);
			dayEvents.clear();
		}
		public Button getButton() {
			return dayButton;
		}
	}
}

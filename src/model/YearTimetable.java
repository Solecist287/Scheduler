package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
		renderEventViews(initDate);
	}

	@Override
	public void createView() {
		yearGrid = new GridPane();
		yearGrid.setStyle("-fx-background-color: white;");
		yearGrid.setHgap(MONTH_PADDING);
		yearGrid.setVgap(MONTH_PADDING);
		yearGrid.setPadding(new Insets(MONTH_PADDING,MONTH_PADDING,MONTH_PADDING,MONTH_PADDING));
		//yearGrid.setGridLinesVisible(true);
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
		//only do render new week if date is in another week than lastDateEntered
		if (d.getYear()!=lastDateEntered.getYear()) {
			//System.out.println("view changed for year: " + d.getYear());
			//update day labels
			updateHeaderLabels(d);
			//restore dayslots to default values
			clearAllEventViews();
			//render events
			renderEventViews(d);
		}
		//System.out.println("year stays the same");
		lastDateEntered = d;
	}

	@Override
	public String toString() {
		return "Year";
	}

	@Override
	public void onEventAdded(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			//System.out.println("rendering");
			addViews(e, lastDateEntered);
		}
	}

	@Override
	public void onEventRemoved(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			//remove from appropriate list based on index
			//recalculate color of specific button(s) background
			for (int i = 0; i < dayslots.size(); i++) {
				Dayslot dayslot = dayslots.get(i);
				if (isRenderableForDayslot(e,dayslot.getDate(lastDateEntered))) {
					dayslot.removeEvent(e);
				}
			}
		}
	}

	@Override
	public void renderEventViews(LocalDate d) {
		LocalDate endOfTimeframe = d.withMonth(12).withDayOfMonth(1).plusDays(MONTH_DAYS_NUM-1);
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			//stop searching once past the end of the week
			if (e.getStartDateTime().toLocalDate().isAfter(endOfTimeframe)) {
				break;//stop searching
			//add timeslot if falls in the current week or intersects week
			}else if (isRenderable(e,d)) {
				addViews(e, d);
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
	public void clearAllEventViews() {
		//turn everything white?
		for (int i = 0; i < dayslots.size(); i++) {
			dayslots.get(i).clear();
		}
	}

	@Override
	public void addViews(Event e, LocalDate d) {
		for (int i = 0; i < dayslots.size(); i++) {
			Dayslot dayslot = dayslots.get(i);
			if (isRenderableForDayslot(e,dayslot.getDate(d))) {
				//System.out.println("called");
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
	
	private LocalDate getDateFromIndex(int index, LocalDate d) {
		int buttonOffset = index%MONTH_DAYS_NUM;
		//System.out.println("buttonOffset: " + buttonOffset);
		//two calls for getParent since dayButton->monthGrid->container
		int monthValue = index/MONTH_DAYS_NUM + 1;
		//System.out.println("monthvalue: " + monthValue);
		return d.withMonth(monthValue)
				.withDayOfMonth(1)
				.with(dayOfWeekTemporalField,1)
				.plusDays(buttonOffset);
	}

	//figure out parameters for onclick...date?
	public void viewDayEventsPopup(LocalDate d) {
		int width = Timetable.WIDTH/2;//should be half of timetable width?
		int height = Timetable.WIDTH/4;
		//create popup stage
		Stage popupStage = new Stage();
		Label header = new Label(TimeUtilities.formatDate(d));
		header.setStyle("-fx-background-color: white;");
		ListView<Event> dayEventsListView = new ListView<Event>();
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
		//set events that intersect with date
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			if (isRenderableForDayslot(e,d)) {
				dayEventsListView.getItems().add(e);
			}
		}
		//create container for header and listview
		VBox popupContent = new VBox(10);
		popupContent.getChildren().addAll(header, dayEventsListView);
		popupContent.setPrefWidth(width);
		popupContent.setPrefHeight(height);
		popupContent.setAlignment(Pos.CENTER);
		popupContent.setStyle("-fx-background-color: white;");
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
				//System.out.println("clicked: " + d.toString());
				viewDayEventsPopup(d);
				
			});
			dayEvents = new ArrayList<Event>();
		}
		public void addEvent(Event e) {
			//System.out.println("added event!");
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
			for (int i = 0; i < dayEvents.size(); i++) {
				Color c = dayEvents.get(i).getBackgroundColor();
				r += c.getRed();
				g += c.getGreen();
				b += c.getBlue();
			}
			r = r/dayEvents.size();
			g = g/dayEvents.size();
			b = b/dayEvents.size();
			//System.out.println("r: " + r*255 + ", g: " + g*255 + ", b: " + b*255);
			dayButton.setStyle(boldStyle 
					+ "-fx-background-color: rgb(" + r*255 + "," + g*255 + "," + b*255 + ");");
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

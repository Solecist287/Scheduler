package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MonthTimetable extends Timetable {
	static final int HEADER_HEIGHT = 40;
	static final int NUM_DAYS = 42;
	List<Label> dayHeaderLabels;
	List<ListView<Event>> eventLists;
	
	ScrollPane monthGridContainer;
	GridPane monthGrid, dayOfWeekHeader;
	
	public MonthTimetable(Stage mainStage, List<Event> events, LocalDate initDate, Locale locale) {
		super(mainStage, events, initDate, locale);
		//set timetable dimensions
		rowNum = 6;
		colNum = 7;
		colSize = WIDTH/colNum;
		rowSize = colSize;
		//create lists
		dayHeaderLabels = new ArrayList<Label>();
		eventLists = new ArrayList<ListView<Event>>();
		//create view
		createView();
		updateHeaderLabels(initDate);
		renderEventViews(initDate);
	}

	private void createHeaderGrid() {
		dayOfWeekHeader = new GridPane();
		//set column restraints
		for (int i = 0; i < colNum; i++) {
			dayOfWeekHeader.getColumnConstraints().add(new ColumnConstraints(colSize));
		}
		dayOfWeekHeader.getRowConstraints().add(new RowConstraints(HEADER_HEIGHT));
		for (int i = 0; i < colNum; i++) {
			String dayOfWeek = lastDateEntered.with(dayOfWeekTemporalField, i+1).getDayOfWeek().toString().toLowerCase();
			String dayOfWeekAbbrev = dayOfWeek.substring(0,1).toUpperCase() + dayOfWeek.substring(1,3);
			//String style = "-fx-border-color: black;";
			Label l = new Label();
			l.setAlignment(Pos.CENTER);
			l.setPrefWidth(colSize);
			l.setPrefHeight(HEADER_HEIGHT);
			l.setText(dayOfWeekAbbrev);
			//add to grid
			dayOfWeekHeader.add(l,i,0);
		}
		dayOfWeekHeader.setStyle("-fx-border-width: 1 1 0 1;" + "-fx-border-color: black;" + "-fx-background-color: white;");
		//headerGrid.setGridLinesVisible(true);
	}
	private void createMonthGrid() {
		monthGrid = new GridPane();
		//set column and row constraints
		for (int i = 0; i < colNum; i++) {
			monthGrid.getColumnConstraints().add(new ColumnConstraints(colSize));
		}
		for (int i = 0; i < rowNum; i++) {
			monthGrid.getRowConstraints().add(new RowConstraints(rowSize));
		}
		//draw tiles
		for (int j = 0; j < rowNum; j++) {
			for (int i = 0; i < colNum; i++) {
				VBox tile = new VBox();//will not be used for first column since we use hour labels
				tile.setPrefWidth(colSize);
				tile.setPrefHeight(rowSize);
				String style = "-fx-background-color: white; -fx-border-color: black black black black;";
				if (j == rowNum-1) {//last row
					if (i == colNum-1) {
						style += "-fx-border-width: 1 1 1 1;";
					}else {
						style += "-fx-border-width: 1 0 1 1;";
					}
				}else{//other rows
					if (i == colNum-1) {
						style += "-fx-border-width: 1 1 0 1;";
					}else {
						style += "-fx-border-width: 1 0 0 1;";
					}
				}
				//set style for container
				tile.setStyle(style);
				//create components for container
				//label stuff
				Label header = new Label();
				header.setAlignment(Pos.CENTER);
				header.setPrefWidth(colSize);
				header.setPrefHeight(HEADER_HEIGHT);
				header.setStyle("-fx-background-color: white;");
				//listview stuff
				ListView <Event> lv = new ListView<Event>();
				lv.setCellFactory(x -> {
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
								setPrefWidth(WIDTH/colNum - PADDING);
								setStyle("-fx-background-color: rgb(" + c.getRed()*255 + "," + c.getGreen()*255 + "," + c.getBlue()*255 + ");"
										+ "-fx-border-color: black;" + "-fx-font-size: 12");
								setOnMouseClicked(event -> {
									viewEventPopup(item);
									updateSelected(false);
								});
								updateSelected(false);
							}
						}
					};
				});
				lv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				
				//add to list
				dayHeaderLabels.add(header);
				eventLists.add(lv);
				//add components to tile
				tile.getChildren().addAll(header,lv);
				//add to grid
				monthGrid.add(tile, i, j);
			}
		}
	}
	
	@Override
	public void createView() {
		//set constraints and populate grids
		createHeaderGrid();
		createMonthGrid();
		//create scrollpane to contain hourlygrid
		monthGridContainer = new ScrollPane();
		monthGridContainer.setPrefHeight(HEIGHT - HEADER_HEIGHT);
		monthGridContainer.setMinWidth(WIDTH + PADDING);
		monthGridContainer.setContent(monthGrid);
		
		//contains the whole view
		VBox container = new VBox();
		container.getChildren().addAll(dayOfWeekHeader, monthGridContainer);
		//set view
		view = container;
	}

	@Override
	public void update(LocalDate d) {
		//only do render new week if date is in another week than lastDateEntered
		if (!d.getMonth().equals(lastDateEntered.getMonth()) || d.getYear()!=lastDateEntered.getYear()) {
			//update day labels
			updateHeaderLabels(d);
			//clear items from listviews
			clearAllEventViews();
			//render events
			renderEventViews(d);
		}
		lastDateEntered = d;
	}

	@Override
	public String toString() {
		return "Month";
	}

	@Override
	public void onEventAdded(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			addViews(e, lastDateEntered);
		}
	}
	@Override
	public void onEventRemoved(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			for (int i = 0; i < eventLists.size(); i++) {
				eventLists.get(i).getItems().remove(e);
        	}
		}
	}
	@Override
	public void renderEventViews(LocalDate d) {
		LocalDate endOfTimeframe = d.withDayOfMonth(1).with(dayOfWeekTemporalField,1).plusDays(NUM_DAYS-1);
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

	@Override
	public boolean isRenderable(Event e, LocalDate d) {
		LocalDateTime start = e.getStartDateTime();
		LocalDateTime end = e.getEndDateTime();
		LocalDate beforeTimeframe = d.withDayOfMonth(1).with(dayOfWeekTemporalField,1).minusDays(1);
		LocalDate afterTimeframe = beforeTimeframe.plusDays(NUM_DAYS+1);
		return start.toLocalDate().isBefore(afterTimeframe) && end.toLocalDate().isAfter(beforeTimeframe)
				&& !end.equals(LocalDateTime.of(beforeTimeframe.plusDays(1), LocalTime.MIDNIGHT));
	}

	@Override
	public void clearAllEventViews() {
		for (int i = 0; i < eventLists.size(); i++) {
			eventLists.get(i).getItems().clear();
		}
	}

	@Override
	public void addViews(Event e, LocalDate d) {
		LocalDate startOfTimeframe = d.withDayOfMonth(1).with(dayOfWeekTemporalField,1);
		LocalDate endOfTimeframe = startOfTimeframe.plusDays(NUM_DAYS-1);
		LocalDate startDate = (e.getStartDateTime().toLocalDate().isBefore(startOfTimeframe))
				? startOfTimeframe : e.getStartDateTime().toLocalDate();
		LocalDate endDate = e.getEndDateTime().toLocalDate();//upper bound
		if (e.getEndDateTime().toLocalDate().isAfter(endOfTimeframe)) {
			endDate = endOfTimeframe;
		}else if (e.getEndDateTime().toLocalTime().equals(LocalTime.MIDNIGHT)) {
			endDate = endDate.minusDays(1);
		}
		int daySpan = (int)startDate.until(endDate, ChronoUnit.DAYS);
		int index = (int)startOfTimeframe.until(startDate, ChronoUnit.DAYS);
		int bound = index + daySpan;
		//System.out.println("upper end bound is " + end);
		do {	
			//System.out.println("iteration");
			ListView<Event> currentListView = eventLists.get(index);
			List<Event> currentList = currentListView.getItems();
			for (int i = 0; i < currentList.size(); i++) {
				Event currentEvent = currentList.get(i);
				if (e.endsBy(currentEvent)) {
					currentList.add(i, e);
					break;
				}else if (i == currentList.size()-1) {
					currentList.add(e);
					break;
				}
			}
			if (currentList.size() == 0) {
				currentList.add(e);
			}
			index++;
		}while(index <= bound);
	}
	private void updateHeaderLabels(LocalDate d) {
		LocalDate date = d.withDayOfMonth(1).with(dayOfWeekTemporalField,1);//first date of first week of month
		for (int i = 0; i < dayHeaderLabels.size(); i++) {
			Label currentLabel = dayHeaderLabels.get(i);
			int dayOfMonth = date.getDayOfMonth();
			if (dayOfMonth == 1) {
				String monthName = date.getMonth().toString().toLowerCase();
				String monthNameAbbrev = monthName.substring(0,1).toUpperCase() + monthName.substring(1,3);
				currentLabel.setText(monthNameAbbrev + " " + date.getDayOfMonth());
			}else {
				currentLabel.setText(date.getDayOfMonth() + "");
			}
			date = date.plusDays(1);
		}
	}
}

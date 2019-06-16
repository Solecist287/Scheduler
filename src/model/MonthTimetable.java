package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MonthTimetable extends Timetable {
	static final int HEADER_HEIGHT = 40;

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
		renderEventNodes(initDate);
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
				VBox container = new VBox();//will not be used for first column since we use hour labels
				container.setPrefWidth(colSize);
				container.setPrefHeight(rowSize);
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
				container.setStyle(style);
				//create components for container
				Label l = new Label();
				l.setAlignment(Pos.CENTER);
				l.setPrefWidth(colSize);
				l.setPrefHeight(HEADER_HEIGHT);
				l.setStyle("-fx-background-color: white;");
				ListView <Event> lv = new ListView<Event>();
				//add to list
				dayHeaderLabels.add(l);
				eventLists.add(lv);
				//add components to container
				container.getChildren().addAll(l,lv);
				//add to grid
				monthGrid.add(container, i, j);
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
			//clear timeslots from list and hourlygrid
			clearAllEventNodes();
			//render events
			renderEventNodes(d);
		}
		lastDateEntered = d;
	}

	@Override
	public String toString() {
		return "Month";
	}

	@Override
	public void onEventAdded(Event e) {
		
	}

	@Override
	public void onEventRemoved(Event e) {
		
	}

	@Override
	public void renderEventNodes(LocalDate d) {
		
	}

	@Override
	public boolean isRenderable(Event e, LocalDate d) {
		return false;
	}

	@Override
	public void clearAllEventNodes() {
		
	}

	@Override
	public void addNodes(Event e, LocalDate d) {
		
	}
	//FIX, just copied from weektimetable
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

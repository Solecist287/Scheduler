package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MonthTimetable extends Timetable {
	static final int HEADER_HEIGHT = 40;
	static final int TABLE_HEIGHT = HEIGHT - HEADER_HEIGHT;//500-20=480...div by 6 yay
	ScrollPane monthGridContainer;
	GridPane monthGrid, headerGrid;
	
	public MonthTimetable(Stage mainStage, List<Event> events, LocalDate initDate, TemporalField dayOfWeekTemporalField) {
		super(mainStage, events, initDate, dayOfWeekTemporalField);
		//set timetable dimensions
		rowNum = 6;
		//rowSize = TABLE_HEIGHT/6; 
		colNum = 7;
		colSize = WIDTH/colNum;
		rowSize = colSize;
		//create lists
		//timeslots = new ArrayList<Timeslot>();
		//create view
		createView();
		renderEventNodes(initDate);
	}

	private void createHeaderGrid() {
		headerGrid = new GridPane();
		//set column restraints
		for (int i = 0; i < colNum; i++) {
			headerGrid.getColumnConstraints().add(new ColumnConstraints(colSize));
		}
		headerGrid.getRowConstraints().add(new RowConstraints(HEADER_HEIGHT));
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
			headerGrid.add(l,i,0);
		}
		headerGrid.setStyle("-fx-border-width: 1 1 0 1;" + "-fx-border-color: black;" + "-fx-background-color: white;");
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
		for (int i = 0; i < colNum; i++) {
			for (int j = 0; j < rowNum; j++) {
				Pane p = new Pane();//will not be used for first column since we use hour labels
				p.setPrefWidth(colSize);
				p.setPrefHeight(rowSize);
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
				p.setStyle(style);
				monthGrid.add(p, i, j);
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
		container.getChildren().addAll(headerGrid, monthGridContainer);
		//set view
		view = container;
	}

	@Override
	public void update(LocalDate d) {
		
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

}

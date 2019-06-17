package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class HourlyTimetable extends Timetable {
	static final int TIME_INCREMENT = 5;
	static final int TIME_LABEL_WIDTH = 42;
	static final int HEADER_HEIGHT = 40;//??
	int dayNum;
	ScrollPane hourlyGridContainer;
	GridPane hourlyGrid, headerGrid;
	
	List<Label> headerLabels;
	List<Timeslot> timeslots;
	public HourlyTimetable(Stage mainStage, List<Event> events, LocalDate initDate, int dayNum, Locale locale) {
		super(mainStage, events, initDate, locale);
		this.dayNum = dayNum;
		//set timetable dimensions
		rowNum = (24*60)/TIME_INCREMENT;//5 min intervals;
		rowSize = TIME_INCREMENT; 
		colNum = dayNum + 1;//first is time label column
		colSize = (WIDTH-TIME_LABEL_WIDTH)/dayNum;
		//create lists
		headerLabels = new ArrayList<Label>();
		timeslots = new ArrayList<Timeslot>();
		//create view
		createView();
	}
	private void createHourlyGrid() {
		hourlyGrid = new GridPane();
		//set column and row constraints
		for (int i = 0; i < colNum; i++) {
			int ccSize = (i == 0) ? TIME_LABEL_WIDTH : colSize;
			hourlyGrid.getColumnConstraints().add(new ColumnConstraints(ccSize));
		}
		for (int i = 0; i < rowNum; i++) {
			hourlyGrid.getRowConstraints().add(new RowConstraints(rowSize));
		}
		//draw tiles
		for (int i = 0; i < colNum; i++) {
			for (int j = 0; j < rowNum; j++) {
				Pane p = new Pane();//will not be used for first column since we use hour labels
				p.setPrefWidth(colSize);
				p.setPrefHeight(rowSize);
				String defaultStyle = "-fx-background-color: white; -fx-border-color: black black black black;";
				if (i==0) {//column for time labels. each label is 1 col x 2 rows 
					if (j%12==0){//show only top border otherwise
						String period = (j/12)<12 ? "am" : "pm";
						int hour = ((j/12)%12==0) ? 12 : (j/12)%12;//converts hour from military to 12-hour time
						Label hourLabel = new Label(" " + hour + period);
						hourLabel.setMinHeight(rowSize*2);
						hourLabel.setMinWidth(colSize);
						hourLabel.setStyle(defaultStyle + "-fx-border-width: 1 0 0 1;");
						hourlyGrid.add(hourLabel, i, j, 1, 3);
						j+=2;
					}else {//draw bottom line and left of other cells 
						if (j == rowNum-1) {//bottom
							p.setStyle(defaultStyle + "-fx-border-width: 0 0 1 1;");
						}else {//rest
							p.setStyle(defaultStyle + "-fx-border-width: 0 0 0 1;");
						}
						hourlyGrid.add(p, i, j);
					}
				}else if (j%12==0) {//start of hour, show only top of cell
					if (i==colNum-1) {
						p.setStyle(defaultStyle + "-fx-border-width: 1 1 0 1;");
					}else {
						p.setStyle(defaultStyle + "-fx-border-width: 1 0 0 1;");
					}
					hourlyGrid.add(p, i, j);
				}else if (j == rowNum - 1){//last row of cell
					if (i==colNum-1) {
						p.setStyle(defaultStyle + "-fx-border-width: 0 1 1 1;");
					}else {
						p.setStyle(defaultStyle + "-fx-border-width: 0 0 1 1;");
					}
					hourlyGrid.add(p, i, j);
				}else {//just draw left and right lines of cell
					if (i==colNum-1) {
						p.setStyle(defaultStyle + "-fx-border-width: 0 1 0 1;");
					}else {
						p.setStyle(defaultStyle + "-fx-border-width: 0 0 0 1;");
					}
					hourlyGrid.add(p, i, j);
				}
			}
		}
	}
	private void createHeaderGrid() {
		headerGrid = new GridPane();
		//set column restraints
		for (int i = 0; i < colNum; i++) {
			int ccSize = (i == 0) ? TIME_LABEL_WIDTH : colSize;
			headerGrid.getColumnConstraints().add(new ColumnConstraints(ccSize));
		}
		headerGrid.getRowConstraints().add(new RowConstraints(HEADER_HEIGHT));
		for (int i = 0; i < colNum; i++) {
			if (i == 0) {
				Pane p = new Pane();
				p.setStyle("-fx-background-color: white");
				headerGrid.add(p,i,0);
			}else {
				Label l = new Label();
				l.setAlignment(Pos.CENTER);
				l.setPrefWidth(colSize);
				l.setPrefHeight(HEADER_HEIGHT);
				l.setStyle(/*"-fx-border-width: 1;" + "-fx-border-color: black;" + */"-fx-background-color: white;");
				//add to list
				headerLabels.add(l);
				//add to grid
				headerGrid.add(l,i,0);
			}
		}
		//set dividing lines between headerLabels
		headerGrid.setStyle("-fx-border-width: 1 1 0 1;" + "-fx-border-color: black;" + "-fx-background-color: white;");
		//headerGrid.setGridLinesVisible(true);
	}
	@Override
	public void createView() {
		//set constraints and populate grids
		createHeaderGrid();
		createHourlyGrid();
		//create scrollpane to contain hourlygrid
		hourlyGridContainer = new ScrollPane();
		hourlyGridContainer.setPrefHeight(HEIGHT - HEADER_HEIGHT);
		hourlyGridContainer.setMinWidth(WIDTH + PADDING);
		hourlyGridContainer.setContent(hourlyGrid);
		//contains the whole view
		VBox container = new VBox();
		container.getChildren().addAll(headerGrid, hourlyGridContainer);
		//set view
		view = container;
	}
	//removes timeslots from list and from hourlygrid
	public void clearAllEventViews() {
		//remove timeslot nodes from hourlygrid
		for (int i = 0; i < timeslots.size(); i++) {
			hourlyGrid.getChildren().remove(timeslots.get(i).getView());
		}
		timeslots.clear();
	}
}

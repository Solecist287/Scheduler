package model;

import java.time.LocalDate;
import java.time.temporal.TemporalField;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

public abstract class HourlyTimetable extends Timetable {
	final int TIME_INCREMENT = 5;
	final int TIME_LABEL_WIDTH = 42;
	int rowNum, rowSize, colNum, colSize, dayNum;
	
	public HourlyTimetable(CalendarModel cmodel, LocalDate initDate, int dayNum, TemporalField dayOfWeekTemporalField) {
		super(cmodel, initDate, dayOfWeekTemporalField);
		this.dayNum = dayNum;
		view = createView();
	}

	@Override
	public Node createView() {
		//init to zero first
		int rowSize = TIME_INCREMENT; 
		int colSize = (WIDTH-TIME_LABEL_WIDTH)/dayNum;
		int rowNum = (24*60)/TIME_INCREMENT;//5 min intervals;
		int colNum = dayNum + 1;//first is time label column
		GridPane timetable = new GridPane();
		//set column and row constraints
		for (int i = 0; i < colNum; i++) {
			int ccSize = (i == 0) ? TIME_LABEL_WIDTH : colSize;
			timetable.getColumnConstraints().add(new ColumnConstraints(ccSize));
		}
		for (int i = 0; i < rowNum; i++) {
			timetable.getRowConstraints().add(new RowConstraints(rowSize));
		}
		//draw and set initial values for timetable
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
						//System.out.println(j + ": " + hour + " " + period);
						Label hourLabel = new Label(hour + period);
						hourLabel.setMinHeight(rowSize*2);
						hourLabel.setMinWidth(colSize);
						hourLabel.setStyle(defaultStyle + "-fx-border-width: 1 0 0 1;");
						timetable.add(hourLabel, i, j, 1, 3);
						j+=2;
					}else {//draw bottom line and left of other cells 
						if (j == rowNum-1) {//bottom
							p.setStyle(defaultStyle + "-fx-border-width: 0 0 1 1;");
						}else {//rest
							p.setStyle(defaultStyle + "-fx-border-width: 0 0 0 1;");
						}
						timetable.add(p, i, j);
					}
				}else if (j%12==0) {//start of hour, show only top of cell
					if (i==colNum-1) {
						p.setStyle(defaultStyle +	"-fx-border-width: 1 1 0 1;");
					}else {
						p.setStyle(defaultStyle + "-fx-border-width: 1 0 0 1;");
					}
					timetable.add(p, i, j);
				}else if (j == rowNum - 1){//last row of cell
					if (i==colNum-1) {
						p.setStyle(defaultStyle + "-fx-border-width: 0 1 1 1;");
					}else {
						p.setStyle(defaultStyle + "-fx-border-width: 0 0 1 1;");
					}
					timetable.add(p, i, j);
				}else {//just draw left and right lines of cell
					if (i==colNum-1) {
						p.setStyle(defaultStyle + "-fx-border-width: 0 1 0 1;");
					}else {
						p.setStyle(defaultStyle + "-fx-border-width: 0 0 0 1;");
					}
					timetable.add(p, i, j);
				}
			}
		}
		return timetable;
	}
	
	//@Override
	//public void updateView(LocalDate date) {}
}

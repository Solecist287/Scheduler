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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class YearTimetable extends Timetable {
	ScrollPane yearGridContainer;
	GridPane yearGrid;
	
	static final int MONTH_ROW_NUM = 7;
	static final int MONTH_COL_NUM = 7;
	static final int MONTH_NUM = 12;
	static final int MONTH_PADDING = 14;
	static final int CONTAINER_PADDING = 10;
	static final int MONTH_NUM_DAYS = 42;
	List<List<Button>> monthlyDayButtons;//each list corresponds to a month
	
	public YearTimetable(Stage mainStage, List<Event> events, LocalDate initDate, Locale locale) {
		super(mainStage, events, initDate, locale);
		//set timetable dimensions
		rowNum = 4;
		colNum = 3;
		colSize = (WIDTH - MONTH_PADDING * (colNum + 1))/colNum;
		rowSize = colSize;
		//create lists
		monthlyDayButtons = new ArrayList<List<Button>>();
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
				int index = 3 * j + i + 1;
				String monthName = Month.of(index).toString().toLowerCase();
				String monthNameCapitalized = monthName.substring(0,1).toUpperCase() + monthName.substring(1);
				
				Label monthNameHeader = new Label(monthNameCapitalized);
				monthNameHeader.setAlignment(Pos.CENTER);
				monthNameHeader.setStyle("-fx-background-color: white;");
				
				int monthGridCellLength = colSize/7;
				List<Button> monthDayButtons = new ArrayList<Button>();
				GridPane monthGrid = new GridPane();
				for (int k = 0; k < MONTH_ROW_NUM; k++) {
					for (int l = 0; l < MONTH_COL_NUM; l++) {
						//set style and other parameters for label
						String style = "-fx-background-color: white; -fx-border-color: black black black black;";
						if (k == MONTH_ROW_NUM-1) {//last row
							if (l == MONTH_COL_NUM-1) {
								style += "-fx-border-width: 1 1 1 1;";
							}else {
								style += "-fx-border-width: 1 0 1 1;";
							}
						}else{//other rows
							if (l == MONTH_COL_NUM-1) {
								style += "-fx-border-width: 1 1 0 1;";
							}else {
								style += "-fx-border-width: 1 0 0 1;";
							}
						}
						//add nodes to correct positions in monthgrid
						if (k == 0) {//if first row, set days of week symbols
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
							Button dayButton = new Button();
							dayButton.setOnAction(e->System.out.println("click!"));
							dayButton.setStyle(style);
							dayButton.setAlignment(Pos.CENTER);
							dayButton.setPrefSize(monthGridCellLength, monthGridCellLength);
							//add to buttons
							monthDayButtons.add(dayButton);
							//add to grid
							monthGrid.add(dayButton, l, k);
						}
					}
				}
				monthlyDayButtons.add(monthDayButtons);
				//add header and monthgrid to container
				VBox container = new VBox();//will not be used for first column since we use hour labels
				container.getChildren().addAll(monthNameHeader, monthGrid);
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
			//clear timeslots from list and hourlygrid
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
			//addViews(e, lastDateEntered);
		}
	}

	@Override
	public void onEventRemoved(Event e) {
		if (isRenderable(e, lastDateEntered)) {
			
		}
	}

	@Override
	public void renderEventViews(LocalDate d) {
		LocalDate endOfTimeframe = d.withMonth(12).withDayOfMonth(1).plusDays(MONTH_NUM_DAYS-1);
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
		LocalDate beforeTimeframe = d.withMonth(1).withDayOfMonth(1).with(dayOfWeekTemporalField,1).minusDays(1);
		LocalDate afterTimeframe = d.withMonth(12).withDayOfMonth(1).plusDays(MONTH_NUM_DAYS);
		System.out.println("renderable: " + (start.toLocalDate().isBefore(afterTimeframe) && end.toLocalDate().isAfter(beforeTimeframe)
				&& !end.equals(LocalDateTime.of(beforeTimeframe.plusDays(1), LocalTime.MIDNIGHT))));
		return start.toLocalDate().isBefore(afterTimeframe) && end.toLocalDate().isAfter(beforeTimeframe)
				&& !end.equals(LocalDateTime.of(beforeTimeframe.plusDays(1), LocalTime.MIDNIGHT));
	}

	@Override
	public void clearAllEventViews() {
		//turn everything white?
	}

	@Override
	public void addViews(Event e, LocalDate d) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateHeaderLabels(LocalDate d) {
		for (int i = 0; i < monthlyDayButtons.size(); i++) {//for each month...
			LocalDate date = d.withMonth(i+1).withDayOfMonth(1).with(dayOfWeekTemporalField,1);//first date of first week of month
			List<Button> monthDayButtons = monthlyDayButtons.get(i);
			for (int j = 0; j < monthDayButtons.size(); j++) {//for each day of the current month...
				int dayOfMonth = date.getDayOfMonth();
				Button currentButton = monthDayButtons.get(j);
				currentButton.setText(dayOfMonth + "");
				date = date.plusDays(1);
			}
		}
	}

}

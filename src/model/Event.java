package model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.scene.paint.Color;

public class Event implements Serializable{
	
	private static final long serialVersionUID = 1175616276017341271L;
	private String title, description;
	private LocalDateTime startDateTime, endDateTime;
	private double red, green, blue;
	private final double opacity = 0;
	//minimal constructor
	public Event(String title, String description, LocalDateTime startDateTime, 
			LocalDateTime endDateTime, Color backgroundColor) {
		this.setTitle(title);
		this.setDescription(description);
		this.setStartDateTime(startDateTime);
		this.setEndDateTime(endDateTime);
		red = backgroundColor.getRed();
		green = backgroundColor.getGreen();
		blue = backgroundColor.getBlue();
	}
	
	public boolean isBefore(Event other) {
		return this.getEndDateTime().isBefore(other.getStartDateTime());
	}
	
	public boolean isAfter(Event other) {
		return this.getStartDateTime().isAfter(other.getEndDateTime());
	}
	
	public boolean isDuring(Event other) {
		return !this.isBefore(other) && !this.isAfter(other);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(LocalDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
	}

	public int getRowSpan() {
		return (int)(Duration.between(startDateTime.toLocalTime(), endDateTime.toLocalTime()).toMinutes())/5;
	}
	public int getRow() {
		LocalTime startTime = startDateTime.toLocalTime();
		return (startTime.getHour() * 60 + startTime.getMinute())/5;
	}
	
	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Color getBackgroundColor() {
		return new Color(red, green, blue, opacity);
	}
	
	public boolean equals(Object o) {
		if (o==null||!(o instanceof Event)) {return false;}
		Event other = (Event)o;
		return this.getStartDateTime().equals(other.getStartDateTime());//can assume just start for now
	}
}


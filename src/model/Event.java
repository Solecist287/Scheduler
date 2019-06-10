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
	//event ends at or before another event
	public boolean endsBy(Event other) {
		return this.getEndDateTime().isBefore(other.getStartDateTime()) 
				|| this.getEndDateTime().equals(other.getStartDateTime());
	}
	//event starts at or after another event's ending
	public boolean startsBy(Event other) {
		return this.getStartDateTime().isAfter(other.getEndDateTime())
				|| this.getStartDateTime().equals(other.getEndDateTime());
	}
	
	public boolean isDuring(Event other) {
		return !this.endsBy(other) && !this.startsBy(other);
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


package model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javafx.scene.paint.Color;

public class Event implements Serializable{
	
	private static final long serialVersionUID = 1175616276017341271L;
	private String title, description;
	private LocalDateTime startDateTime, endDateTime;
	private Color backgroundColor;
	
	//minimal constructor
	public Event(String title, String description, LocalDateTime startDateTime, 
			LocalDateTime endDateTime, Color backgroundColor) {
		this.setTitle(title);
		this.setDescription(description);
		this.setStartDateTime(startDateTime);
		this.setEndDateTime(endDateTime);
		this.setBackgroundColor(backgroundColor);
	}
	//ends before
	public boolean isBefore(Event other) {
		return this.getEndDateTime().isBefore(other.getStartDateTime());
	}
	//starts after
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

	public void setEndDateTime(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}


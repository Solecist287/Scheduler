package model;

import java.io.Serializable;
import java.time.LocalDateTime;

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
		this.title = title;
		this.description = description;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
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

	public String getDescription() {
		return description;
	}

	public LocalDateTime getStartDateTime() {
		return startDateTime;
	}

	public LocalDateTime getEndDateTime() {
		return endDateTime;
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


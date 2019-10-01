package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class CalendarModel implements Serializable {

	public static final String storeDir = "./data";
	public static final String storeFile = "events.dat";
	
	private static final long serialVersionUID = 631182814265361496L;
	private List<Event> events;
	
	private CalendarModel() {
		events = new ArrayList<Event>();
	}
	public ObservableList<Event> getEvents(){
		return FXCollections.observableList(events);
	}
	public static void writeData(CalendarModel cmodel){
		try{
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(storeDir + File.separator + storeFile));//not sure if append should be false
			oos.writeObject(cmodel);
			oos.close();
		}catch(IOException e) {
			System.out.println("write:error opening file");
		}		
	}
	
	
	public static CalendarModel readData(){
		try {
			ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(storeDir + File.separator + storeFile));
			CalendarModel cmodel = (CalendarModel)ois.readObject();
			ois.close();
			return cmodel;
		}catch(Exception e) {
			System.out.println("oh no");
			return new CalendarModel();
		}
	}
	
	public void printEvents() {
		String output = "---EVENTS---\n";
		for (int i = 0; i < events.size(); i++) {
			Event currentEvent = events.get(i);
			output+=currentEvent.getTitle() + " " + currentEvent.getStartDateTime() + " - " + currentEvent.getEndDateTime() + "\n";
		}
		System.out.println(output);
	}
	
	public static class Event implements Serializable{
		
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
}

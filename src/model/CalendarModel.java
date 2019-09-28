package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CalendarModel implements Serializable {

	public static final String storeDir = "./data";
	public static final String storeFile = "events.dat";
	
	private static final long serialVersionUID = 631182814265361496L;
	private List<Event> events;
	
	public CalendarModel() {
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
	
}

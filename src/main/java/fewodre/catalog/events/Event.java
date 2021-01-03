package fewodre.catalog.events;

import fewodre.utils.Place;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.useraccount.UserAccount;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@Entity
public class Event extends Product {

	public enum EventType {
		LARGE, SMALL
	}

	private String eventCompanyUuid;
	private String description;
	private String image;
	private Place place;
	private boolean eventStatus;
	private EventType eventType;
	private int capacity;
	private LocalDate date;
	private LocalTime time;
	private int repeats;
	private int repeateRate;

	public Event(String title, String eventCompanyUuid, String description, String image, Place place,
			boolean eventStatus, EventType eventType, int capacity, MonetaryAmount price, LocalDate date,
			LocalTime time, int repeats, int repeateRate) {
		super(title, price);
		this.description = description;
		this.eventCompanyUuid = eventCompanyUuid;
		this.eventStatus = eventStatus;
		this.eventType = eventType;
		this.image = image;
		this.place = place;
		this.capacity = capacity;
		this.addCategory("Event");
		this.date = date;
		this.time = time;
		this.repeats = repeats;
		this.repeateRate = repeateRate;
	}

	public Event() {
		super("template_title", Money.parse("EUR 5"));
		this.addCategory("Event");
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventCompanyUuid() {
		return eventCompanyUuid;
	}

	public void setEventCompanyUuid(String eventCompanyUuid) {
		this.eventCompanyUuid = eventCompanyUuid;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public boolean isEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(boolean eventStatus) {
		this.eventStatus = eventStatus;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public void setRepeats(int repeats) {
		this.repeats = repeats;
	}

	public int getRepeats() {
		return repeats;
	}

	public void setRepeateRate(int repeateRate) {
		this.repeateRate = repeateRate;
	}

	public int getRepeateRate() {
		return repeateRate;
	}

	public ArrayList<LocalDate> getAllDates() {
		ArrayList<LocalDate> AllDates = new ArrayList<LocalDate>();
		LocalDate myDate = getDate();
		int myRepeats = getRepeats();
		int myRepeateRate = getRepeateRate();
		AllDates.add(myDate);
		if (myRepeats == 0) {
			return AllDates;
		}
		while (myRepeats > 1) {
			myDate = myDate.plusDays(myRepeateRate);
			AllDates.add(myDate);
			myRepeats--;
		}
		return AllDates;
	}

	public ArrayList<LocalDate> getPossibleDates(LocalDate StartDate, LocalDate EndDate) {
		ArrayList<LocalDate> AllPossDates = new ArrayList<LocalDate>();
		ArrayList<LocalDate> AllDates = getAllDates();
		for (int i = 0; i < AllDates.size(); i++) {
			if ((AllDates.get(i).isAfter(StartDate) && AllDates.get(i).isBefore(EndDate))
					|| AllDates.get(i).equals(StartDate) || AllDates.get(i).equals(EndDate)) {
				AllPossDates.add(AllDates.get(i));
			}
		}
		return AllPossDates;
	}
}

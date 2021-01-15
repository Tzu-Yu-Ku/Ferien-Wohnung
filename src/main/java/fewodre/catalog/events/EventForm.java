package fewodre.catalog.events;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import fewodre.catalog.CatalogForm;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import fewodre.catalog.events.Event.EventType;
import fewodre.utils.Place;

public class EventForm extends CatalogForm {
	private String eventCompany;
	private LocalDate date;
	private LocalTime time;
	private int repeats;
	private int repeateRate;

	public EventForm() {
		//Nested comment so that sonarqube doesnt detect this empty constructor as code smell.
	}

	public String getEventCompanyUuid() {
		return eventCompany;
	}

	public void setEventCompanyUuid(String eventCompany) {
		this.eventCompany = eventCompany;
	}

	public EventType getEventType() {
		if (getRepeats() > 0) {
			return EventType.SMALL;
		}
		return EventType.LARGE;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(String dateString) {
		this.date = LocalDate.parse(dateString);
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(String timeString) {
		this.time = LocalTime.parse(timeString);
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

	public Event toNewEvent(String userAccount) {
		return new Event(getName(), userAccount, getDescription(), "event1.png",
				new Place(getStreet(), getNumber(), getPostalnumber(), getCity(), getCoordinateX(), getCoordinateY()),
				true, getEventType(), getCapacity(), Money.of(getPrice(), "EUR"), getDate(),
				getTime(), getRepeats(), getRepeateRate());
	}
}

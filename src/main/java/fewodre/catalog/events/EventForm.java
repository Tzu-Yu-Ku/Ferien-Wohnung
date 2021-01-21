package fewodre.catalog.events;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import fewodre.catalog.CatalogForm;
import org.apache.tomcat.jni.Local;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import fewodre.catalog.events.Event.EventType;
import fewodre.utils.Place;

public class EventForm extends CatalogForm {

	private String eventCompany;
	private LocalDate date;
	private LocalTime time;
	private String eventType;

	public EventForm() {
		// No logic in this constructor is required.
	}

	public String getEventCompanyUuid() {
		return eventCompany;
	}

	public void setEventCompanyUuid(String eventCompany) {
		this.eventCompany = eventCompany;
	}

	public EventType getEventType() {
		if (eventType.equals("small")) {
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


	public Event toNewEvent(String userAccount) {
		return new Event(getName(), userAccount, getDescription(), "event1.png",
				new Place(getStreet(), getNumber(), getPostalnumber(), getCity(), getCoordinateX(), getCoordinateY()),
				true, getEventType(), getCapacity(), Money.of(getPrice(), "EUR"), getDate(),
				getTime());
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
}

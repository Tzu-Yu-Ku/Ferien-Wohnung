package fewodre.catalog;

import fewodre.utils.Place;
import org.salespointframework.catalog.Product;

import javax.persistence.ElementCollection;
import java.util.List;

public class Event extends Product {

	public static enum EventType {
		LARGE, SMALL
	}

	private EventType eventType;
	private String description, eventCompanyUuid;
	private Place place;
	private boolean eventStatus;

	public Event() {
		Event test = new Event();
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
}

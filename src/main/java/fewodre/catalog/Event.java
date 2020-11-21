package fewodre.catalog;

import fewodre.utils.Place;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class Event extends Product {

	public static enum EventType {
		LARGE, SMALL
	}

	private String eventCompanyUuid;
	private String description;
	private String image;
	private Place place;
	private boolean eventStatus;
	private EventType eventtype;

	public Event(String eventCompanyUuid, String description, String image, Place place, boolean eventStatus, EventType eventtype) {
		
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

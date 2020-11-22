package fewodre.catalog.events;

import fewodre.utils.Place;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

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
	private Money price;

	public Event(String title, String eventCompanyUuid, String description, String image, Place place, boolean eventStatus, EventType eventType, int capacity, Number price) {
		super(title, Money.of(price, "EUR"), Metric.UNIT);
		this.description = description;
		this.eventCompanyUuid = eventCompanyUuid;
		this.eventStatus = eventStatus;
		this.eventType = eventType;
		this.image = image;
		this.place = place;
		this.capacity = capacity;
	}

	public Event() {
		super("template_title", Money.parse("EUR 1"), Metric.UNIT);
	}

	public Money getPrice(){
		return price;
	}

	public void setPrice(Money price){
		this.price = price;
	}

	public String getImage(){
		return image;
	}

	public void setImage(String image){
		this.image = image;
	}

	public int getCapacity(){
		return capacity;
	}

	public void setCapacity(int capacity){
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
}

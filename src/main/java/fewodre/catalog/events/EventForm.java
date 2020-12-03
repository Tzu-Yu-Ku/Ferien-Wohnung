package fewodre.catalog.events;

import org.javamoney.moneta.Money;
import fewodre.catalog.events.Event.EventType;
import fewodre.utils.Place;

public class EventForm {
    private String name;
    private String description;
    private String eventCompanyUuid;
    private String street;
    private String number;
    private String postalnumber;
    private String city;
    private int capacity;
    private String eventType;
    private int price;

    public EventForm(){
        this.name = name;
        this.description = description;
        this.eventCompanyUuid = eventCompanyUuid;
        this.street = street;
        this.number = number;
        this.postalnumber = postalnumber;
        this.city = city;
        this.capacity = capacity;
        this.eventType = eventType;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalnumber() {
        return postalnumber;
    }

    public void setPostalnumber(String postalnumber) {
        this.postalnumber = postalnumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public EventType stringToEvent(String eType){
        System.out.println(eType);
        if(eType.equals("big")){
            return EventType.LARGE;
        }
        return EventType.SMALL;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Event toNewEvent() {
        return new Event(getName(),getEventCompanyUuid(),getDescription(),"event1.png",new Place(getStreet(), getNumber(), getPostalnumber(), getCity(), 1, 1), true, stringToEvent(getEventType()),getCapacity(),Money.of(getPrice(), "EUR"));
	}


}

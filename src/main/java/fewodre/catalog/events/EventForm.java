package fewodre.catalog.events;

import java.time.LocalDate;
import java.time.LocalTime;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccount;

import fewodre.catalog.events.Event.EventType;
import fewodre.utils.Place;

public class EventForm {
    private String name;
    private String description;
    private String eventCompany;
    private String street;
    private String number;
    private String postalnumber;
    private String city;
    private int capacity;
    private int price;
    private LocalDate date;
    private LocalTime time;
    private int repeats;
    private int repeateRate;
    private int coordX;
    private int coordY;

    public EventForm() {
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
        return eventCompany;
    }

    public void setEventCompanyUuid(String eventCompany) {
        this.eventCompany = eventCompany;
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

    public EventType getEventType() {
        if (getRepeats() > 0) {
            return EventType.SMALL;
        }
        return EventType.LARGE;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public Event toNewEvent(String userAccount) {
        return new Event(getName(), userAccount, getDescription(), "event1.png",
                new Place(getStreet(), getNumber(), getPostalnumber(), getCity(), 1, 1), true, getEventType(),
                getCapacity(), Money.of(getPrice(), "EUR"), getDate(), getTime(), getRepeats(), getRepeateRate());
    }
}

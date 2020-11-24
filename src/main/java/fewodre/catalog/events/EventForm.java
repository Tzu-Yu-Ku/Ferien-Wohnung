package fewodre.catalog.events;

import org.javamoney.moneta.Money;

import fewodre.catalog.events.Event.EventType;
import fewodre.utils.Place;

public class EventForm {
    private String name;
    private String description;
    private String EventCompanyUuid;

    public EventForm(){
        this.name = name;
        this.description = description;
        this.EventCompanyUuid = EventCompanyUuid;
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
        return EventCompanyUuid;
    }

    public void setEventCompanyUuid(String EventCompanyUuid) {
        this.EventCompanyUuid = EventCompanyUuid;
    }

    public Event toNewEvent() {
		return new Event(getName(),getEventCompanyUuid(),getDescription(),"event1.png",new Place("An der Frauenkirche", "1", "01234", "Dresden", 1, 1), true, EventType.SMALL,5,Money.parse("EUR 5"));
	}
}

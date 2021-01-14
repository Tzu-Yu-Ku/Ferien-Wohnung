package fewodre.catalog.holidayhomes;

import fewodre.catalog.events.Event;
import fewodre.utils.Place;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 * This class represents Holisay
 */
@Entity
public class HolidayHome extends Product {

	@ManyToMany
	public List<Event> acceptedEvents;

	private String hostMail;
	private String description;
	private String image;
	private Place place;
	private boolean isBookable;
	private int capacity;

	public HolidayHome(String title, String hostMail, String description, String image, Place place, boolean isBookable,
			int capacity, MonetaryAmount price) {
		super(title, price);
		this.description = description;
		this.hostMail = hostMail;
		this.image = image;
		this.place = place;
		this.isBookable = isBookable;
		this.capacity = capacity;
		this.addCategory("HolidayHome");
	}

	public HolidayHome() {
		super("template_title", Money.parse("EUR 5"));
		this.addCategory("HolidayHome");
	}

	/**
	 * Method gives the capacity of a HolidayHome.
	 *
	 * @return Returns a natural number to describes the capacity of a HolidayHome.
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Method set the capacity of a HolidayHome to a given one.
	 *
	 * @param capacity 	natural number to describes the capacity of a HolidayHome
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * Method gives the description of a HolidayHome.
	 *
	 * @return Returns a String to describes the description of a HolidayHome.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Method set the description of a HolidayHome to a given one.
	 *
	 * @param description String to describes the capacity of a HolidayHome
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Method gives the hostMail of a HolidayHome.
	 *
	 * @return Returns a String to describes the hostMail of a HolidayHome.
	 */
	public String getHostMail() {
		return hostMail;
	}

	/**
	 * Method set the hostMail of a HolidayHome to a given one.
	 *
	 * @param hostMail String to describes the hostMail of a HolidayHome
	 */
	public void setHostMail(String hostMail) {
		this.hostMail = hostMail;
	}

	/**
	 * Method gives the image of a HolidayHome.
	 *
	 * @return Returns a String to describes the image of a HolidayHome.
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Method set the image of a HolidayHome to a given one.
	 *
	 * @param image String to describes the image of a HolidayHome
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * Method gives the place of a HolidayHome.
	 *
	 * @return Returns an object of class Place to describes the place of a HolidayHome.
	 */
	public Place getPlace() {
		return place;
	}

	/**
	 * Method set the place of a HolidayHome to a given one.
	 *
	 * @param place Object of class Place to describes the place of a HolidayHome.
	 */
	public void setPlace(Place place) {
		this.place = place;
	}

	/**
	 * Method gives the isBookable of a HolidayHome.
	 *
	 * @return Returns a boolean to describes the isBookable of a HolidayHome.
	 */
	public boolean getIsBookable() {
		return isBookable;
	}

	/**
	 * Method set the isBookable of a HolidayHome to a given one.
	 *
	 * @param bookable Boolean to describes the isBookable of a HolidayHome.
	 */
	public void setIsBookable(boolean bookable) {
		isBookable = bookable;
	}

	/**
	 * Method is add a object of class Event to the List acceptedEvents.
	 *
	 * @param event Object of class Event.
	 */
	public void acceptEvent(Event event) {
		if (!acceptedEvents.contains(event)) {
			acceptedEvents.add(event);
		}
	}

	/**
	 * Method gives the acceptedEvents a List of Events
	 *
	 * @return Returns acceptedEvents a List of Events.
	 */
	public List<Event> getAcctivatEvents() {
		return acceptedEvents;
	}

}

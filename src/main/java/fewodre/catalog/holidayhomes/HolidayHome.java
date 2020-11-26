package fewodre.catalog.holidayhomes;

import fewodre.utils.Place;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Metric;

import javax.persistence.Entity;

/**
 * This class represents Holisay
 */
@Entity
public class HolidayHome extends Product {

	private String hostUuid;
	private String description;
	private String image;
	private Place place;
	private boolean isBookable;
	private int capacity;

	public HolidayHome(String hostUuid, String description, String image, Place place, boolean isBookable, int capacity, Money price) {
		this.description = description;
		this.hostUuid = hostUuid;
		this.image = image;
		this.place = place;
		this.isBookable = isBookable;
		this.capacity = capacity;
	}

	public HolidayHome() {
		super("template_title", Money.parse("EUR 1"), Metric.UNIT);
		//this.addCategory("home");
	}

	public int getCapacity(){
		return capacity;
	}

	public void setCapacity(int capacity){
		this.capacity = capacity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHostUuid() {
		return hostUuid;
	}

	public void setHostUuid(String hostUuid) {
		this.hostUuid = hostUuid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public boolean isBookable() {
		return isBookable;
	}

	public void setBookable(boolean bookable) {
		isBookable = bookable;
	}

	public HolidayHome createHome(String hostUuid, String description, String image, Place place, boolean isBookable, int capacity, Money price) {
		return new HolidayHome(hostUuid, description, image, place, isBookable, capacity, price);
	}
}

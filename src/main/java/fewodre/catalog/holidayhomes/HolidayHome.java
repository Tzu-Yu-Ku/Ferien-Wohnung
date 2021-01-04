package fewodre.catalog.holidayhomes;

import fewodre.utils.Place;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import javax.money.MonetaryAmount;
import javax.persistence.Entity;

/**
 * This class represents Holisay
 */
@Entity
public class HolidayHome extends Product {

	private String hostMail;
	private String description;
	private String image;
	private Place place;
	private boolean isBookable;
	private int capacity;

	public HolidayHome(String title, String hostMail, String description, String image, Place place, boolean isBookable, int capacity, MonetaryAmount price) {
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

	public String getHostMail() {
		return hostMail;
	}

	public void setHostMail(String hostMail) {
		this.hostMail = hostMail;
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

	public boolean getIsBookable() {
		return isBookable;
	}

	public void setIsBookable(boolean bookable) {
		isBookable = bookable;
	}

}

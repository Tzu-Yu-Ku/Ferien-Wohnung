package fewodre.catalog;

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

	private String description, hostUuid;
	//@ElementCollection
	private String image;
	private Place place;
	private boolean isBookable;

	public HolidayHome(String description, String hostUuid, String image, Place place, boolean isBookable) {
		this.description = description;
		this.hostUuid = hostUuid;
		this.image = image;
		this.place = place;
		this.isBookable = isBookable;
	}

	public HolidayHome() {
		super("template_title", Money.parse("EUR 1"), Metric.UNIT);
		this.addCategory("home");
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
}

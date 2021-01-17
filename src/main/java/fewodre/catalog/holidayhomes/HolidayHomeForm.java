package fewodre.catalog.holidayhomes;

import fewodre.catalog.CatalogForm;
import org.javamoney.moneta.Money;
import fewodre.utils.Place;

public class HolidayHomeForm extends CatalogForm {
	private String hostMail;

	public HolidayHomeForm() {
		// No logic in this constructor is required.
	}

	public String getHostMail() {
		return hostMail;
	}

	public void setHostMail(String hostMail) {
		this.hostMail = hostMail;
	}

	public HolidayHome toNewHolidayHome(String hostMail) {
		return new HolidayHome(getName(), hostMail, getDescription(), "house4.png",
				new Place(getStreet(), getNumber(), getPostalnumber(), getCity(), getCoordinateX(), getCoordinateY()),
				true, getCapacity(), Money.of(getPrice(), "EUR"));
	}

}
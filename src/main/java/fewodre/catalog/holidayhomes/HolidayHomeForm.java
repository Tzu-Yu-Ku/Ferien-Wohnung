package fewodre.catalog.holidayhomes;

import org.javamoney.moneta.Money;
import fewodre.utils.Place;

public class HolidayHomeForm {
	private String name;
	private String description;
	private String hostMail;
	private String street;
	private String number;
	private String postalnumber;
	private String city;
	private int capacity;
	private int price;
	private int coordinateX;
	private int coordinateY;

	public HolidayHomeForm() {
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

	public String getHostMail() {
		return hostMail;
	}

	public void setHostMail(String hostMail) {
		this.hostMail = hostMail;
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getCoordinateX() {
		return coordinateX;
	}

	public void setCoordinateX(int coordinateX) {
		this.coordinateX = coordinateX;
	}

	public int getCoordinateY() {
		return coordinateY;
	}

	public void setCoordinateY(int coordinateY) {
		this.coordinateY = coordinateY;
	}

	public HolidayHome toNewHolidayHome(String hostMail) {
		return new HolidayHome(getName(), hostMail, getDescription(), "house4.png",
				new Place(getStreet(), getNumber(), getPostalnumber(), getCity(), getCoordinateX(), getCoordinateY()),
				true, getCapacity(), Money.of(getPrice(), "EUR"));
	}

}
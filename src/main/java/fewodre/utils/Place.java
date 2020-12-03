package fewodre.utils;

import java.io.Serializable;

public class Place implements Serializable {

	private String street, houseNumber, postalCode, city;
	private int coordX, coordY;

	public Place(String street, String houseNumber, String postalCode, String city, int coordX, int coordY) {
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.coordX = coordX;
		this.coordY = coordY;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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
}

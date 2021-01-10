package fewodre.utils;

import java.io.Serializable;

public class Place implements Serializable {

	private String street, houseNumber, postalCode, city, district;
	private int coordX, coordY;

	public Place(String street, String houseNumber, String postalCode, String city, int coordX, int coordY) {
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.district = "Stadt";
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

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	@Override
	public String toString() {
		return street + " " + houseNumber + ", " + city + ", " + coordX + ", " + coordY + ", D: " + district;
	}

	// --------------------------------------- Distanz-Berechnung

	public int distanceToOtherPlaces(Place place2) {
		System.out.println(toString());
		// System.out.println(place2.toString());
		return (int) Math.round(Math.sqrt(Math.pow(Math.abs(getCoordX() - place2.getCoordX()), 2)
				+ Math.pow(Math.abs(getCoordY() - place2.getCoordY()), 2)));
	}
}

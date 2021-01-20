package fewodre.catalog;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CatalogForm {


	@NotBlank(message = "Der Angebotsname darf nicht leer sein.")
	private String name;

	@NotBlank(message = "Die Beschreibung darf nicht leer sein.")
	private String description;

	@NotBlank(message = "Die Straße darf nicht leer sein.")
	private String street;

	@NotBlank(message = "Die Hausnummer darf nicht leer sein.")
	@Pattern(regexp = "\\d+[a-zA-Z]*", message = "Bitte geben Sie eine gültige Hausnummer ein.")
	private String number;

	@NotBlank(message = "Die PLZ darf nicht leer sein.")
	@Size(min = 5, max = 5, message = "Geben Sie eine gültige PLZ ein.")
	private String postalnumber;

	@NotBlank(message = "Die Stadt darf nicht leer sein.")
	private String city;

	@Min(value = 1, message = "Die Personenzahl muss mindestens 1 sein.")
	private int capacity;

	@Min(value = 0, message = "Der Preis muss ein positiver Wert sein.")
	private int price;

	private MultipartFile imageupload;

	private int coordinateX;
	private int coordinateY;

	public CatalogForm() {
		// No logic in this constructor is required.
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

	public MultipartFile getImageupload() {
		return imageupload;
	}

	public void setImageupload(MultipartFile imageupload) {
		this.imageupload = imageupload;
	}
}

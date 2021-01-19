package fewodre.useraccounts;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PersonalForm extends RegistrationForm {

	@NotBlank(message = "{RegistrationForm.password_confirm.NotEmpty}")
	@DateTimeFormat(pattern = "yyyy-mm-dd")
	private final String birthDate;

	@NotBlank(message = "{RegistrationForm.street.NotEmpty}")
	private final String street;

	@NotBlank(message = "{RegistrationForm.housenumber.NotEmpty}")
	private final String houseNumber;

	@NotBlank(message = "{RegistrationForm.postcode.Invalid}")
	@Size(min = 5, max = 5, message = "Geben Sie eine gültige Postleitzahl ein.")
	private final String postcode;

	@NotBlank(message = "{RegistrationForm.city.NotEmpty}")
	private final String city;

	public PersonalForm(String firstName, String lastName, String email, String password, String passwordConfirm,
	                    String birthDate, String street, String houseNumber, String postcode, String city) {
		super(firstName, lastName, email, password, passwordConfirm);
		this.birthDate = birthDate;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postcode = postcode;
		this.city = city;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public String getPostcode() {
		return postcode;
	}

	public String getCity() {
		return city;
	}
}

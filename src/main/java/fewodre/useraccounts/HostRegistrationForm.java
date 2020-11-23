package fewodre.useraccounts;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

class HostRegistrationForm {

	@NotBlank(message = "{RegistrationForm.firstname.NotEmpty}")
	private final String firstName;

	@NotBlank(message = "{RegistrationForm.lastname.NotEmpty}")
	private final String lastName;

	@NotBlank(message = "{RegistrationForm.email.NotEmpty}")
	@Email(message = "{RegistrationForm.email.NotValid}")
	private final String email;

	@NotBlank(message = "{RegistrationForm.password.NotEmpty}")
	private final String password;

	@NotBlank(message = "{RegistrationForm.password_confirm.NotEmpty}")
	private final String passwordConfirm;

	@NotBlank(message = "{RegistrationForm.password_confirm.NotEmpty}")
	@DateTimeFormat(pattern = "yyyy-mm-dd")
	private final String birthDate;

	@NotBlank(message = "{RegistrationForm.street.NotEmpty}")
	private final String street;

	@NotBlank(message = "{RegistrationForm.housenumber.NotEmpty}")
	private final String houseNumber;

	@NotBlank(message = "{RegistrationForm.postcode.NotEmpty}")
	@Size(min=5, max=5, message = "PLZ muss fünfstellig sein.")
	private final String postcode;

	@NotBlank(message = "{RegistrationForm.city.NotEmpty}")
	private final String city;

	@NotBlank(message = "{RegistrationForm.iban.NotEmpty}")
	private final String iban;

	@NotBlank(message = "{RegistrationForm.bic.NotEmpty}")
	private final String bic;


	public HostRegistrationForm(String firstName, String lastName, String email, String password, String passwordConfirm,
								  String birthDate, String street, String houseNumber, String postcode, String city, String iban, String bic) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
		this.birthDate = birthDate;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postcode = postcode;
		this.city = city;
		this.iban = iban;
		this.bic = bic;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
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

	public String getIban() {
		return iban;
	}

	public String getBic() {
		return bic;
	}
}
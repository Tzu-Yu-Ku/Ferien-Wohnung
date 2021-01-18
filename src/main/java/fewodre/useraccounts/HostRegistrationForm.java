package fewodre.useraccounts;

import org.hibernate.annotations.ListIndexBase;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

class HostRegistrationForm extends PersonalForm {

	@NotBlank(message = "{RegistrationForm.iban.NotEmpty}")
	@Pattern(regexp = "^DE\\d{2}[ ]\\d{4}[ ]\\d{4}[ ]\\d{4}[ ]\\d{4}[ ]\\d{2}|DE\\d{20}$",
			message = "Keine korrekte IBAN eingegeben!")
	private final String iban;

	@NotBlank(message = "{RegistrationForm.bic.NotEmpty}")
	@Pattern(regexp = "^([a-zA-Z]){6}([0-9a-zA-Z]){2}([0-9a-zA-Z]{3})?$",
			message = "Keine korrekte BIC eingegeben!")
	private final String bic;


	public HostRegistrationForm(String firstName, String lastName, String email, String password,
	                            String passwordConfirm, String birthDate, String street, String houseNumber,
	                            String postcode, String city, String iban, String bic) {
		super(firstName, lastName, email, password, passwordConfirm, birthDate, street, houseNumber, postcode, city);
		this.iban = iban;
		this.bic = bic;
	}

	public String getIban() {
		return iban;
	}

	public String getBic() {
		return bic;
	}
}
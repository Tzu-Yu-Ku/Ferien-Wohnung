package fewodre.useraccounts;

import javax.validation.constraints.NotBlank;

class HostRegistrationForm extends PersonalForm {

	@NotBlank(message = "{RegistrationForm.iban.NotEmpty}")
	private final String iban;

	@NotBlank(message = "{RegistrationForm.bic.NotEmpty}")
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
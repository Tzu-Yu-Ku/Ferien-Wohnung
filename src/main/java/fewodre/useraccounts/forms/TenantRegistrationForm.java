package fewodre.useraccounts.forms;

import javax.validation.constraints.AssertTrue;

public class TenantRegistrationForm extends PersonalForm {

	@AssertTrue(message = "{RegistrationForm.terms.NotAgreed}")
	private final Boolean terms;

	public TenantRegistrationForm(String firstName, String lastName, String email, String password,
	                              String passwordConfirm, String birthDate, String street, String houseNumber,
	                              String postcode, String city, Boolean terms) {
		super(firstName, lastName, email, password, passwordConfirm, birthDate, street, houseNumber, postcode, city);
		this.terms = terms;
	}

	public Boolean getTerms() {
		return terms;
	}
}
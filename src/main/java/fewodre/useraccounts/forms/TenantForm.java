package fewodre.useraccounts.forms;

import javax.validation.constraints.AssertTrue;

public class TenantForm extends PersonalForm {

	@AssertTrue(message = "{RegistrationForm.terms.NotAgreed}")
	private final Boolean terms;

	public TenantForm(String firstName, String lastName, String email, String password,
	                  String passwordConfirm, String birthDate, String street, String houseNumber,
	                  String postcode, String city, Boolean terms) {
		super(firstName, lastName, email, password, passwordConfirm, birthDate, street, houseNumber, postcode, city);
		this.terms = terms;
	}

	public Boolean getTerms() {
		return terms;
	}
}
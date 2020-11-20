package fewodre.useraccounts;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

class RegistrationForm {

	@NotBlank(message = "{RegistrationForm.username.NotEmpty}")
	private final String firstName;

	@NotBlank(message = "{RegistrationForm.username.NotEmpty}")
	private final String lastName;

	@NotBlank(message = "{RegistrationForm.password.NotEmpty}")
	private final String password;

	@NotBlank(message = "{RegistrationForm.address.NotEmpty}")
	@Email(message = "{RegistrationForm.address.NotValid}")
	private final String email;

	@AssertTrue(message = "{RegistrationForm.terms.notAgreed}")
	private final Boolean terms;

	public RegistrationForm(String firstName, String lastName, String password, String email, Boolean terms) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.terms = terms;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public Boolean getTerms() {
		return terms;
	}

}
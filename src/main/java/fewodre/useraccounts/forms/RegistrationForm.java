package fewodre.useraccounts.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class RegistrationForm {

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

	public RegistrationForm(String firstName,String lastName, String email, String password,
	                        String passwordConfirm) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
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

}

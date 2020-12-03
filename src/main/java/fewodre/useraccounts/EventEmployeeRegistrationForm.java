package fewodre.useraccounts;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

class EventEmployeeRegistrationForm {

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

	@NotBlank(message = "{RegistrationForm.eventcompany.NotEmpty}")
	private final String eventCompany;



	public EventEmployeeRegistrationForm(String firstName, String lastName, String email, String password, String passwordConfirm,
								  String eventCompany) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
		this.eventCompany = eventCompany;
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

	public String getEventCompany() {
		return eventCompany;
	}

}
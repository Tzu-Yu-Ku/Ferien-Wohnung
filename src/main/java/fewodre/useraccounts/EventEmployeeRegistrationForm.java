package fewodre.useraccounts;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

class EventEmployeeRegistrationForm {

	@NotBlank(message = "{RegistrationForm.eventCompany.NotEmpty}")
	private final String eventCompany;

	@NotBlank(message = "{RegistrationForm.email.NotEmpty}")
	@Email(message = "{RegistrationForm.email.NotValid}")
	private final String email;

	@NotBlank(message = "{RegistrationForm.password.NotEmpty}")
	private final String password;

	@NotBlank(message = "{RegistrationForm.password_confirm.NotEmpty}")
	private final String passwordConfirm;


	public EventEmployeeRegistrationForm(String eventCompany, String email, String password, String passwordConfirm) {
		this.eventCompany = eventCompany;
		this.email = email;
		this.password = password;
		this.passwordConfirm = passwordConfirm;
	}

	public String getEventCompany() {
		return eventCompany;
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
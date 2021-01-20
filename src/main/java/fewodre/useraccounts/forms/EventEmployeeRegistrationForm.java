package fewodre.useraccounts.forms;

import javax.validation.constraints.NotBlank;

public class EventEmployeeRegistrationForm extends RegistrationForm {

	@NotBlank(message = "{RegistrationForm.eventcompany.NotEmpty}")
	private final String eventCompany;

	public EventEmployeeRegistrationForm(String firstName, String lastName, String email, String password,
	                                     String passwordConfirm, String eventCompany) {
		super(firstName, lastName, email, password, passwordConfirm);
		this.eventCompany = eventCompany;
	}

	public String getEventCompany() {
		return eventCompany;
	}

}
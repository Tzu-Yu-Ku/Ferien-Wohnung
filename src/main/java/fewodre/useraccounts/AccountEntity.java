package fewodre.useraccounts;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@GeneratedValue(strategy = GenerationType.AUTO)
	private String uuid;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@Temporal(TemporalType.DATE)
	private Date birthDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;

	@NotBlank
	private String street;

	@NotBlank
	private String houseNumber;

	@NotBlank
	private String postalCode;

	@NotBlank
	private String city;

	@OneToOne
	private UserAccount account;

	public String getUuid() {
		return uuid;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public String getCity() {
		return city;
	}
	
	public UserAccount getAccount() {
		return account;
	}

	public String getId() {
		return id;
	}
}

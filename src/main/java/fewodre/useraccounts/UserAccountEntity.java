package fewodre.useraccounts;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.zip.DataFormatException;

@Entity
public class UserAccountEntity {

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
	private UserAccount userAccount;

	public String getId() {
		return id;
	}
}

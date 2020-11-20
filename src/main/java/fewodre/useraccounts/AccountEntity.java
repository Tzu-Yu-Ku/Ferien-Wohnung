package fewodre.useraccounts;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@NotBlank
	private String uuid;

	@Temporal(TemporalType.DATE)
	private Date birthDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;

	@NotBlank
	private String street;

	@NotBlank
	private String houseNumber;

	@NotBlank
	private String postCode;

	@NotBlank
	private String city;

	@OneToOne
	private UserAccount account;

	public AccountEntity() {
	}

	public String getUuid() {
		return uuid;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getCity() {
		return city;
	}


	public AccountEntity setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public AccountEntity setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public AccountEntity setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
		return this;
	}

	public AccountEntity setStreet(String street) {
		this.street = street;
		return this;
	}

	public AccountEntity setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
		return this;
	}

	public AccountEntity setPostCode(String postalCode) {
		this.postCode = postalCode;
		return this;
	}

	public AccountEntity setCity(String city) {
		this.city = city;
		return this;
	}

	public AccountEntity setAccount(UserAccount account) {
		this.account = account;
		return this;
	}
}

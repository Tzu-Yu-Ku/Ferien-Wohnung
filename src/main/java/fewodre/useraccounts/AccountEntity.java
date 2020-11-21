package fewodre.useraccounts;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String uuid;

	@NotBlank
	private String birthDate;

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

	public UserAccount getAccount() {
		return this.account;
	}


	public AccountEntity setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public AccountEntity setBirthDate(String birthDate) {
		this.birthDate = birthDate;
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

	@Override
	public String toString() {
		return "AccountEntity{" +
				"id=" + id +
				", uuid='" + uuid + '\'' +
				", birthDate='" + birthDate + '\'' +
				", street='" + street + '\'' +
				", houseNumber='" + houseNumber + '\'' +
				", postCode='" + postCode + '\'' +
				", city='" + city + '\'' +
				", account=" + account +
				'}';
	}
}

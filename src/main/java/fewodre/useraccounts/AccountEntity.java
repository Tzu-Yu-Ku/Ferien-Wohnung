package fewodre.useraccounts;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String uuid;

	@NotBlank
	private String eventCompany;

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

	@NotBlank
	private String iban;

	@NotBlank
	private String bic;

	@OneToOne
	private UserAccount account;

	public AccountEntity() {
		// Spring requires an empty constructor for entities.
	}

	public String getUuid() {
		return uuid;
	}

	public String getBirthDate(){
		return birthDate;
	}

	public String getEventCompany() {
		return eventCompany;
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

	public String getIban() {
		return iban;
	}

	public String getBic() {
		return bic;
	}

	public UserAccount getAccount() {
		return this.account;
	}


	public AccountEntity setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public AccountEntity setEventCompany(String companyName) {
		this.eventCompany = companyName;
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

	public AccountEntity setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public AccountEntity setBic(String bic) {
		this.bic = bic;
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
				", companyName='" + eventCompany + '\'' +
				", birthDate='" + birthDate + '\'' +
				", street='" + street + '\'' +
				", houseNumber='" + houseNumber + '\'' +
				", postCode='" + postCode + '\'' +
				", city='" + city + '\'' +
				", iban='" + iban + '\'' +
				", bic='" + bic + '\'' +
				", account=" + account +
				'}';
	}
}

package fewodre.useraccounts;

import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

@Service
@Transactional
public class AccountManagement {

	public static final Role TENANT_ROLE = Role.of("TENANT");
	public static final Role UNACTIVATED_TENANT_ROLE = Role.of("UNACTIVATED_TENANT");
	public static final Role HOST_ROLE = Role.of("HOST");
	public static final Role EVENTEMPLOYEE_ROLE = Role.of("EVENT_EMPLOYEE");
	public static final Role ADMIN_ROLE = Role.of("ADMIN");


	private static final Logger LOG = LoggerFactory.getLogger(AccountManagement.class);

	private final AccountRepository accounts;
	private final UserAccountManagement userAccounts;

	@Autowired
	AccountManagement(AccountRepository accounts, UserAccountManagement userAccounts) {

		Assert.notNull(accounts, "CustomerRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.accounts = accounts;
		this.userAccounts = userAccounts;
	}

	public AccountEntity createTenantAccount(TenantRegistrationForm tenantRegistrationForm) {

		Assert.notNull(tenantRegistrationForm, "registrationForm should not be null!");
		if (Password.UnencryptedPassword.of(tenantRegistrationForm.getPassword())
				.equals(Password.UnencryptedPassword.of(tenantRegistrationForm.getPasswordConfirm()))) {
			Password.UnencryptedPassword password = Password.UnencryptedPassword.of(tenantRegistrationForm.getPassword());
			if (userAccounts.findByUsername(tenantRegistrationForm.getEmail()).isEmpty()) {
				UserAccount newUserAccount = userAccounts.create(tenantRegistrationForm.getEmail(), password,
						tenantRegistrationForm.getEmail(), UNACTIVATED_TENANT_ROLE);
				newUserAccount.setFirstname(tenantRegistrationForm.getFirstName());
				newUserAccount.setLastname(tenantRegistrationForm.getLastName());
				newUserAccount.setEnabled(false);
				AccountEntity newAccount = new AccountEntity().setUuid(UUID.randomUUID().toString())
						.setBirthDate(tenantRegistrationForm.getBirthDate())
						.setStreet(tenantRegistrationForm.getStreet())
						.setHouseNumber(tenantRegistrationForm.getHouseNumber())
						.setPostCode(tenantRegistrationForm.getPostcode())
						.setCity(tenantRegistrationForm.getCity())
						.setAccount(newUserAccount)
						.setIban("NO_IBAN")
						.setBic("NO_BIC")
						.setEventCompany("NO_COMPANY");
				LOG.info(newAccount.getUuid());
				return accounts.save(newAccount);
			} else {
				return null;
			}
		} else {
			return null;
		}

	}

	public AccountEntity createHostAccount(HostRegistrationForm hostRegistrationForm) {

		Assert.notNull(hostRegistrationForm, "registrationForm should not be null!");
		if (Password.UnencryptedPassword.of(hostRegistrationForm.getPassword()).equals(Password.UnencryptedPassword.of(hostRegistrationForm.getPasswordConfirm()))) {
			Password.UnencryptedPassword password = Password.UnencryptedPassword.of(hostRegistrationForm.getPassword());
			if (userAccounts.findByUsername(hostRegistrationForm.getEmail()).isEmpty()) {
				UserAccount newUserAccount = userAccounts.create(hostRegistrationForm.getEmail(), password,
						hostRegistrationForm.getEmail(), HOST_ROLE);
				newUserAccount.setFirstname(hostRegistrationForm.getFirstName());
				newUserAccount.setLastname(hostRegistrationForm.getLastName());
				AccountEntity newAccount = new AccountEntity().setUuid(UUID.randomUUID().toString())
						.setBirthDate(hostRegistrationForm.getBirthDate())
						.setStreet(hostRegistrationForm.getStreet())
						.setHouseNumber(hostRegistrationForm.getHouseNumber())
						.setPostCode(hostRegistrationForm.getPostcode())
						.setCity(hostRegistrationForm.getCity())
						.setIban(hostRegistrationForm.getIban())
						.setBic(hostRegistrationForm.getBic())
						.setEventCompany("NO_COMPANY")
						.setAccount(newUserAccount);
				LOG.info(newAccount.getUuid());
				return accounts.save(newAccount);
			} else {
				return null;
			}
		} else {
			return null;
		}

	}

	public AccountEntity createEventEmployeeAccount(EventEmployeeRegistrationForm eventEmployeeRegistrationForm) {

		Assert.notNull(eventEmployeeRegistrationForm, "registrationForm should not be null!");
		if (Password.UnencryptedPassword.of(eventEmployeeRegistrationForm.getPassword()).equals(Password.UnencryptedPassword.of(eventEmployeeRegistrationForm.getPasswordConfirm()))) {
			Password.UnencryptedPassword password = Password.UnencryptedPassword.of(eventEmployeeRegistrationForm.getPassword());
			if (userAccounts.findByUsername(eventEmployeeRegistrationForm.getEmail()).isEmpty()) {
				UserAccount newUserAccount = userAccounts.create(eventEmployeeRegistrationForm.getEmail(), password,
						eventEmployeeRegistrationForm.getEmail(), EVENTEMPLOYEE_ROLE);
				newUserAccount.setFirstname(eventEmployeeRegistrationForm.getFirstName());
				newUserAccount.setLastname(eventEmployeeRegistrationForm.getLastName());
				AccountEntity newAccount = new AccountEntity().setUuid(UUID.randomUUID().toString())
						.setBirthDate("NO_BIRTHDATE")
						.setStreet("NO_STREET")
						.setHouseNumber("NO_HOUSE_NUMBER")
						.setPostCode("NO_POSTCODE")
						.setCity("NO_CITY")
						.setIban("NO_IBAN")
						.setBic("NO_BIC")
						.setEventCompany(eventEmployeeRegistrationForm.getEventCompany())
						.setAccount(newUserAccount);
				LOG.info(newAccount.getUuid());
				return accounts.save(newAccount);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public ArrayList<AccountEntity> findByRole(Role role) {
		Streamable<UserAccount> allUserAccounts = userAccounts.findAll();
		ArrayList<AccountEntity> accountEntitiesByRole = new ArrayList<>();
		for (UserAccount userAccount : allUserAccounts.toList()) {
			if (userAccount.getRoles().toList().contains(role)) {
				AccountEntity accountEntity = accounts.findByAccount(userAccount);
				if (accountEntity != null) {
					accountEntitiesByRole.add(accountEntity);
				}
			}
		}
		LOG.info(accountEntitiesByRole.toString());
		return accountEntitiesByRole;
	}

	public Streamable<UserAccount> findAllDisabled() {
		return userAccounts.findDisabled();
	}

	public Boolean enableTenant(String tenant_username) {
		userAccounts.enable(userAccounts.findByUsername(tenant_username).get().getId());
		userAccounts.findByUsername(tenant_username).get().add(TENANT_ROLE);
		userAccounts.findByUsername(tenant_username).get().remove(UNACTIVATED_TENANT_ROLE);
		return userAccounts.findByUsername(tenant_username).get().isEnabled();
	}

	public Boolean deleteAccount(String username) {
		return deleteAccount(userAccounts.findByUsername(username).get());
	}

	public Boolean deleteAccount(UserAccount userAccount) {
		accounts.deleteAccountEntityByAccount(userAccount);
		UserAccount deletedUserAccount = userAccounts.delete(userAccount);
//		LOG.info(deletedUserAccount.toString());
		return true;
	}

	public AccountRepository getRepository(){
		return accounts;
	}

}





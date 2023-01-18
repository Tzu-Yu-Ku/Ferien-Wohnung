package fewodre.useraccounts;

import fewodre.useraccounts.forms.EventEmployeeForm;
import fewodre.useraccounts.forms.HostForm;
import fewodre.useraccounts.forms.TenantForm;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
	public AccountManagement(AccountRepository accounts, UserAccountManagement userAccounts) {

		Assert.notNull(accounts, "CustomerRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.accounts = accounts;
		this.userAccounts = userAccounts;
	}

	public AccountEntity createTenantAccount(TenantForm tenantForm) {

		Assert.notNull(tenantForm, "registrationForm should not be null!");
		if (Password.UnencryptedPassword.of(tenantForm.getPassword())
				.equals(Password.UnencryptedPassword.of(tenantForm.getPasswordConfirm()))) {
			Password.UnencryptedPassword password = Password
					.UnencryptedPassword
					.of(tenantForm.getPassword());
			if (userAccounts.findByUsername(tenantForm.getEmail()).isEmpty()) {
				UserAccount newUserAccount = userAccounts.create(tenantForm.getEmail(), password,
						tenantForm.getEmail(), UNACTIVATED_TENANT_ROLE);
				newUserAccount.setFirstname(tenantForm.getFirstName());
				newUserAccount.setLastname(tenantForm.getLastName());
				newUserAccount.setEnabled(false);
				AccountEntity newAccount = new AccountEntity().setUuid(UUID.randomUUID().toString())
						.setBirthDate(tenantForm.getBirthDate())
						.setStreet(tenantForm.getStreet())
						.setHouseNumber(tenantForm.getHouseNumber())
						.setPostCode(tenantForm.getPostcode())
						.setCity(tenantForm.getCity())
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

	public AccountEntity createHostAccount(HostForm hostForm) {

		Assert.notNull(hostForm, "registrationForm should not be null!");
		if (Password.UnencryptedPassword.of(hostForm.getPassword())
				.equals(Password.UnencryptedPassword.of(hostForm.getPasswordConfirm()))) {
			Password.UnencryptedPassword password = Password.UnencryptedPassword.of(hostForm.getPassword());
			if (userAccounts.findByUsername(hostForm.getEmail()).isEmpty()) {
				UserAccount newUserAccount = userAccounts.create(hostForm.getEmail(), password,
						hostForm.getEmail(), HOST_ROLE);
				newUserAccount.setFirstname(hostForm.getFirstName());
				newUserAccount.setLastname(hostForm.getLastName());
				AccountEntity newAccount = new AccountEntity().setUuid(UUID.randomUUID().toString())
						.setBirthDate(hostForm.getBirthDate())
						.setStreet(hostForm.getStreet())
						.setHouseNumber(hostForm.getHouseNumber())
						.setPostCode(hostForm.getPostcode())
						.setCity(hostForm.getCity())
						.setIban(hostForm.getIban())
						.setBic(hostForm.getBic())
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

	public AccountEntity createEventEmployeeAccount(EventEmployeeForm eventEmployeeForm) {

		Assert.notNull(eventEmployeeForm, "registrationForm should not be null!");
		if (Password.UnencryptedPassword.of(eventEmployeeForm.getPassword())
				.equals(Password.UnencryptedPassword.of(eventEmployeeForm.getPasswordConfirm()))) {
			Password.UnencryptedPassword password = Password
					.UnencryptedPassword
					.of(eventEmployeeForm.getPassword());
			if (userAccounts.findByUsername(eventEmployeeForm.getEmail()).isEmpty()) {
				UserAccount newUserAccount = userAccounts.create(eventEmployeeForm.getEmail(), password,
						eventEmployeeForm.getEmail(), EVENTEMPLOYEE_ROLE);
				newUserAccount.setFirstname(eventEmployeeForm.getFirstName());
				newUserAccount.setLastname(eventEmployeeForm.getLastName());
				AccountEntity newAccount = new AccountEntity().setUuid(UUID.randomUUID().toString())
						.setBirthDate("NO_BIRTHDATE")
						.setStreet("NO_STREET")
						.setHouseNumber("NO_HOUSE_NUMBER")
						.setPostCode("NO_POSTCODE")
						.setCity("NO_CITY")
						.setIban("NO_IBAN")
						.setBic("NO_BIC")
						.setEventCompany(eventEmployeeForm.getEventCompany())
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

	public AccountRepository getRepository() {
		return accounts;
	}

}





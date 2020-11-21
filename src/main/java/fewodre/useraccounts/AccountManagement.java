package fewodre.useraccounts;

import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Date;
import java.util.UUID;

@Service
@Transactional
public class AccountManagement {

	public static final Role TENANT_ROLE = Role.of("TENANT");
	public static final Role HOST_ROLE = Role.of("HOST");
	public static final Role EVENTEMPLOYEE_ROLE = Role.of("EVENT_EMPLOYEE");
	public static final Role ADMIN_ROLE = Role.of("ADMIN");

	private static final Logger LOG = LoggerFactory.getLogger(AccountDataInitializer.class);

	private final AccountRepository accounts;
	private final UserAccountManagement userAccounts;

	@Autowired
	AccountManagement(AccountRepository accounts, UserAccountManagement userAccounts) {

		Assert.notNull(accounts, "CustomerRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.accounts = accounts;
		this.userAccounts = userAccounts;
	}

	public AccountEntity createTenantAccount(RegistrationForm registrationForm) {

		Assert.notNull(registrationForm, "registrationForm should not be null!");

		Password.UnencryptedPassword password = Password.UnencryptedPassword.of(registrationForm.getPassword());
		if(userAccounts.findByUsername(registrationForm.getEmail()).isEmpty()) {
			UserAccount newUserAccount = userAccounts.create(registrationForm.getEmail(), password,
					registrationForm.getEmail(), TENANT_ROLE);
			newUserAccount.setFirstname(registrationForm.getFirstName());
			newUserAccount.setLastname(registrationForm.getLastName());
			AccountEntity newAccount = new AccountEntity().setUuid(UUID.randomUUID().toString())
					.setBirthDate(registrationForm.getBirthDate())
					.setStreet(registrationForm.getStreet())
					.setHouseNumber(registrationForm.getHouseNumber())
					.setPostCode(registrationForm.getPostcode())
					.setCity(registrationForm.getCity())
					.setAccount(newUserAccount);
			LOG.info(newAccount.getUuid());
			return accounts.save(newAccount);
		}
		else {
			return null;
		}

	}

}

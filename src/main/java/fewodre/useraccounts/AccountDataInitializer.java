package fewodre.useraccounts;


import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static fewodre.useraccounts.AccountManagement.*;

@Component
public class AccountDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(AccountDataInitializer.class);

	private final AccountManagement accountManagement;
	private final UserAccountManagement userAccountManagement;

	AccountDataInitializer(AccountManagement accountManagement, UserAccountManagement userAccountManagement) {
		Assert.notNull(accountManagement, "accountManagement must not be null!");
		Assert.notNull(userAccountManagement, "userAccountManagement must not be null!");

		this.accountManagement = accountManagement;
		this.userAccountManagement = userAccountManagement;
	}

	@Override
	public void initialize() {
		// Skip creation if database was already populated
		if (userAccountManagement.findByUsername("janu@log.in").isPresent()) {
			return;
		}

		LOG.info("Creating mock accounts to populate our database and test our prototype.");

		userAccountManagement.create("admin", Password.UnencryptedPassword.of("admin"), ADMIN_ROLE);
		userAccountManagement.create("host", Password.UnencryptedPassword.of("host"), HOST_ROLE);
		userAccountManagement.create("tenant", Password.UnencryptedPassword.of("tenant"), TENANT_ROLE);
		userAccountManagement.create("event", Password.UnencryptedPassword.of("event"), EVENTEMPLOYEE_ROLE);

	}
}

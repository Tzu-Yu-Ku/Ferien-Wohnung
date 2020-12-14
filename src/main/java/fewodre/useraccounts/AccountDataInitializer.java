package fewodre.useraccounts;


import org.apache.catalina.Host;
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
		if (userAccountManagement.findByUsername("admin").isPresent()) {
			return;
		}

		LOG.info("Creating mock accounts to populate our database and test our prototype.");

		TenantRegistrationForm tenantRegistrationForm = new TenantRegistrationForm("Janujan", "Jan",
				"tenant@tenant", "123", "123", "1900-01-01", "Test Street",
				"1", "12345", "Dresden", true);
//		HostRegistrationForm hostRegistrationForm = new HostRegistrationForm("Janujan", "Jan",
//				"host@host", "123", "123", "1900-01-01", "Test Street",
//				"1", "12345", "Dresden", "DE55500105171938297534", "MALADE51AKI");
//		EventEmployeeRegistrationForm eventEmployeeRegistrationForm = new EventEmployeeRegistrationForm("Janujan", "Jan",
//				"event@employee", "123", "123", "EventBois Dresden GmbH");

		accountManagement.createTenantAccount(tenantRegistrationForm);
//		accountManagement.createHostAccount(hostRegistrationForm);
//		accountManagement.createEventEmployeeAccount(eventEmployeeRegistrationForm);

		userAccountManagement.create("admin", Password.UnencryptedPassword.of("admin"), ADMIN_ROLE);
		userAccountManagement.create("host", Password.UnencryptedPassword.of("host"), HOST_ROLE);
		userAccountManagement.create("tenant", Password.UnencryptedPassword.of("tenant"), TENANT_ROLE);
		userAccountManagement.create("event", Password.UnencryptedPassword.of("event"), EVENTEMPLOYEE_ROLE);

	}
}

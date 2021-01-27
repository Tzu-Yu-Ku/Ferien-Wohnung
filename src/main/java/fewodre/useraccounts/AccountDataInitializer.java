package fewodre.useraccounts;


import fewodre.bookings.BookingDataInitializer;

import fewodre.useraccounts.forms.EventEmployeeForm;
import fewodre.useraccounts.forms.HostForm;
import fewodre.useraccounts.forms.TenantForm;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
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
	private final BookingDataInitializer bookingDataInitializer;

	AccountDataInitializer(AccountManagement accountManagement, UserAccountManagement userAccountManagement,
	                       BookingDataInitializer bookingDataInitializer) {
		Assert.notNull(accountManagement, "accountManagement must not be null!");
		Assert.notNull(userAccountManagement, "userAccountManagement must not be null!");

		this.accountManagement = accountManagement;
		this.userAccountManagement = userAccountManagement;
		this.bookingDataInitializer = bookingDataInitializer;
	}

	@Override
	public void initialize() {
		// Skip creation if database was already populated
		if (userAccountManagement.findByUsername("admin").isPresent()) {
			return;
		}

		LOG.info("Creating mock accounts to populate our database and test our prototype.");

		TenantForm tenantForm = new TenantForm("Kunde", "Account",
				"tenant@tenant", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", true);
		HostForm hostForm = new HostForm("Vermieter", "Account",
				"host@host", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", "DE55500105171938297534", "MALADE51AKI");
		HostForm hostForm2 = new HostForm("Vermieter2", "Account2",
				"neuer@host", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", "DE55500105171938297534", "MALADE51AKI");
		EventEmployeeForm eventEmployeeForm = new EventEmployeeForm(
				"Eventmitarbeiter", "Employee",
				"event@employee", "123", "123", "EventBois Dresden GmbH");

		accountManagement.createTenantAccount(tenantForm);
		accountManagement.createHostAccount(hostForm);
		accountManagement.createHostAccount(hostForm2);
		accountManagement.createEventEmployeeAccount(eventEmployeeForm);

		userAccountManagement.create("admin", Password.UnencryptedPassword.of("admin"), ADMIN_ROLE);

		TenantForm testingTenantAccountForm = new TenantForm("Kunde", "Account",
				"test@test", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", true);
		accountManagement.createTenantAccount(testingTenantAccountForm);
		accountManagement.enableTenant("test@test");
		bookingDataInitializer.initialize();
	}
}

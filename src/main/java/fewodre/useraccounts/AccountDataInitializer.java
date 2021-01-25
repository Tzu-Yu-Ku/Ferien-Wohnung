package fewodre.useraccounts;



import fewodre.bookings.BookingDataInitializer;

import fewodre.useraccounts.forms.EventEmployeeRegistrationForm;
import fewodre.useraccounts.forms.HostRegistrationForm;
import fewodre.useraccounts.forms.TenantRegistrationForm;

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

	AccountDataInitializer(AccountManagement accountManagement, UserAccountManagement userAccountManagement, BookingDataInitializer bookingDataInitializer) {
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

		TenantRegistrationForm tenantRegistrationForm = new TenantRegistrationForm("Kunde", "Account",
				"tenant@tenant", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", true);
		HostRegistrationForm hostRegistrationForm = new HostRegistrationForm("Vermieter", "Account",
				"host@host", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", "DE55500105171938297534", "MALADE51AKI");
		HostRegistrationForm hostRegistrationForm2 = new HostRegistrationForm("Vermieter2", "Account2",
				"neuer@host", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", "DE55500105171938297534", "MALADE51AKI");
		EventEmployeeRegistrationForm eventEmployeeRegistrationForm = new EventEmployeeRegistrationForm(
				"Eventmitarbeiter", "Employee",
				"event@employee", "123", "123", "EventBois Dresden GmbH");

		accountManagement.createTenantAccount(tenantRegistrationForm);
		accountManagement.createHostAccount(hostRegistrationForm);
		accountManagement.createHostAccount(hostRegistrationForm2);
		accountManagement.createEventEmployeeAccount(eventEmployeeRegistrationForm);

		userAccountManagement.create("admin", Password.UnencryptedPassword.of("admin"), ADMIN_ROLE);

		TenantRegistrationForm testingTenantAccountForm = new TenantRegistrationForm("Kunde", "Account",
				"test@test", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", true);
		accountManagement.createTenantAccount(testingTenantAccountForm);
		accountManagement.enableTenant("test@test");
		bookingDataInitializer.initialize();
	}
}

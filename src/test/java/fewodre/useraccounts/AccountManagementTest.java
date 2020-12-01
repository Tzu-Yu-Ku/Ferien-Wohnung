package fewodre.useraccounts;

import fewodre.FeWoDre;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FeWoDre.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountManagementTest {

	@Autowired
	private AccountRepository accounts;

	@Autowired
	private UserAccountManagement userAccounts;

	@Autowired
	private AccountManagement accountManagement;

	public TenantRegistrationForm tenantRegistrationForm() {
		return new TenantRegistrationForm("Sinthu", "Jan", "sinthu@jan.de", "mongo",
				"mongo", "1993-11-07", "Reisewitzer Straße", "22", "0159",
				"Dresden", true);
	}

	@Test
	void createTenantAccount() {
		accounts.deleteAll();
		accountManagement.createTenantAccount(tenantRegistrationForm());
		assertEquals(1,accounts.count());
		assertEquals(1, userAccounts.findAll().stream().count());
	}

	@Test
	void createHostAccount() {

	}

	@Test
	void createEventEmployeeAccount() {

	}

	@Test
	void testCreateTenantAccount() {

	}

	@Test
	void testCreateHostAccount() {
	}


	@Test
	void testCreateEventEmployeeAccount() {

	}
}
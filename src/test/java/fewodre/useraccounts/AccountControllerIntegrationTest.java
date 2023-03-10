package fewodre.useraccounts;

import fewodre.useraccounts.forms.EventEmployeeForm;
import fewodre.useraccounts.forms.HostForm;
import fewodre.useraccounts.forms.TenantForm;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

	@Autowired
	private AccountManagement accountManagement;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserAccountManagement userAccounts;

	@Autowired
	private AccountController accountController;

	@Autowired
	MockMvc mvc;

	@Test
	void registerAdminGet() throws Exception {

		Model model = new ExtendedModelMap();
		TenantForm tenantForm = new TenantForm("Kunde", "Account",
				"tenant@tenant", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", true);
		String returnedView = accountController.registerAdmin(model, tenantForm);
		assertThat(returnedView).isEqualTo("register");

		mvc.perform(get("/register")).andExpect(status().isOk());

	}


	@Test
	void registerNewAccount() throws Exception {
		TenantForm tenantForm = new TenantForm("Kunde", "Account",
				"unit@test", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", true);
		mvc.perform(post("/register")
				.flashAttr("tenantForm", tenantForm))
				.andExpect(status().isFound());
		accountManagement.enableTenant("unit@test");
	}

	@Test
	void activateTenants() throws Exception {

		Model model = new ExtendedModelMap();
		String returnedView = accountController.activateTenants(model);
		assertThat(returnedView).isEqualTo("accounts/activatetenants");

		mvc.perform(get("/activatetenants"))
				.andExpect(model().attributeExists("unactivatedtenants"))
				.andExpect(status().isOk());

	}

	@Test
	void postActivateTenants() {
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void editUser() throws Exception {
		Streamable<UserAccount> allDisabled = accountManagement.findAllDisabled();
		mvc.perform(get("/activatetenants")
				.param("tenant_username", "test@test"))
				.andExpect(model().attributeExists("unactivatedtenants"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void postEditUserAsAdmin() throws Exception {

		mvc.perform(post("/manageaccount")
				.param("tenant_username", "test@test")
				.param("firstname", "New_First_Name")
				.param("lastname", "New_Last_Name")
				.param("password", "123")
				.param("birthdate", "2000-01-01")
				.param("street", "Street")
				.param("housenumber", "1")
				.param("postcode", "12345")
				.param("city", "Dresden")
				.param("iban", "DE40500105175632313593")
				.param("bic", "MALADE61AKI"))
				.andExpect(status().isOk());

		MvcResult mvcResult = mvc.perform(get("/manageaccounts")).andReturn();
		Object unactivatedTenants = mvcResult.getModelAndView().getModel().get("activatedTenants");
		ArrayList<AccountEntity> accountEntites = (ArrayList<AccountEntity>) unactivatedTenants;

		AccountEntity accountEntity = accountEntites.get(0);
		for (AccountEntity acc : accountEntites) {
			if (acc.getAccount().getUsername() == "test@test") {
				accountEntity = acc;
			}
		}
		assertThat(accountEntity.getAccount().getFirstname()).isEqualTo("New_First_Name");
		assertThat(accountEntity.getAccount().getLastname()).isEqualTo("New_Last_Name");
	}

	@Test
	void registerHostNoPermission() throws Exception {
		mvc.perform(get("/newhost"))
				.andExpect(redirectedUrlPattern("**/login"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void registerNewHostGet() throws Exception {
		mvc.perform(get("/newhost"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void registerHostPost() throws Exception {
		HostForm hostForm = new HostForm("Vermieter", "Account",
				"test@host", "123", "123", "1999-01-01", "Test Street",
				"1", "12345", "Dresden", "DE55500105171938297534", "MALADE51AKI");

		mvc.perform(post("/newhost")
				.flashAttr("hostForm", hostForm))
				.andExpect(status().isFound());

		MvcResult mvcResult = mvc.perform(get("/manageaccounts")).andReturn();
		Object hostAccounts = mvcResult.getModelAndView().getModel().get("hostAccounts");
		ArrayList<AccountEntity> accountEntites = (ArrayList<AccountEntity>) hostAccounts;

		boolean accountWasCreated = false;
		for (AccountEntity accountEntity : accountEntites) {
			if (accountEntity.getAccount().getUsername().equals("test@host")) {
				accountWasCreated = true;
			}
		}
		assertThat(accountWasCreated).isTrue();
	}

	@Test
	void registerEventEmployeeNoPermission() throws Exception {
		mvc.perform(get("/neweventemployee"))
				.andExpect(redirectedUrlPattern("**/login"))
				.andExpect(status().isFound());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void registerEventEmployee() throws Exception {
		mvc.perform(get("/neweventemployee"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void registerNewEventEmployee() throws Exception {
		EventEmployeeForm eventEmployeeForm = new EventEmployeeForm(
				"Eventmitarbeiter", "Employee",
				"test@employee", "123", "123", "EventBois Dresden GmbH");

		MvcResult eventEmployeeRegistrationForm1 = mvc.perform(post("/neweventemployee")
				.flashAttr("eventEmployeeForm", eventEmployeeForm))
				.andExpect(status().isFound())
				.andReturn();

		MvcResult mvcResult = mvc.perform(get("/manageaccounts")).andReturn();
		Object eventAccounts = mvcResult.getModelAndView().getModel().get("eventAccounts");
		ArrayList<AccountEntity> accountEntites = (ArrayList<AccountEntity>) eventAccounts;

		boolean accountWasCreated = false;
		for (AccountEntity accountEntity : accountEntites) {
			if (accountEntity.getAccount().getUsername().equals("test@employee")) {
				accountWasCreated = true;
			}
		}
		assertThat(accountWasCreated).isTrue();
	}

	@Test
	void manageAccounts() {
	}

	@Test
	void activateTenant() {
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void deleteAccount() throws Exception {
		registerNewAccount();
		MvcResult result = mvc.perform(post("/deleteaccount")
				.param("account_username", "unit@test"))
				.andExpect(status().isFound())
				.andReturn();

		MvcResult mvcResult = mvc.perform(get("/manageaccounts")).andReturn();
		Object unactivatedTenants = mvcResult.getModelAndView().getModel().get("activatedTenants");
		ArrayList<AccountEntity> accountEntites = (ArrayList<AccountEntity>) unactivatedTenants;

		AccountEntity accountEntity = null;
		for (AccountEntity acc : accountEntites) {
			if (acc.getAccount().getUsername().equals("unit@test")) {
				accountEntity = acc;
			}
		}
		assertThat(accountEntity).isNull();
	}

}
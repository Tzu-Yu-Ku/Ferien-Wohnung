package fewodre.useraccounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class AccountManagementUnitTest {

	AccountManagement accountManagement;
	UserAccountManagement userAccountManagement;
	AccountRepository accountRepository;

	@BeforeEach
	void setUp() {
		accountRepository = mock(AccountRepository.class);
		when(accountRepository.save(any())).then(i -> i.getArgument(0));

		userAccountManagement = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccountManagement.create(any(), any(), any(), any())).thenReturn(userAccount);

		accountManagement = new AccountManagement(accountRepository, userAccountManagement);
	}

	@Test
	public void createTenantAccount() {

		TenantRegistrationForm form = new TenantRegistrationForm("Firstname", "Lastname",
				"e@mail.com", "123", "123", "1999-01-01", "Street",
				"1", "12345", "City", true);
		AccountEntity tenantAccountEnitity = accountManagement.createTenantAccount(form);

		verify(userAccountManagement, times(1)) //
				.create(eq(form.getEmail()), //
						eq(Password.UnencryptedPassword.of(form.getPassword())), //
						eq(form.getEmail()), //
						eq(AccountManagement.UNACTIVATED_TENANT_ROLE));

		assertThat(tenantAccountEnitity.getAccount()).isNotNull();
	}

	@Test
	void createHostAccount() {

		HostRegistrationForm form = new HostRegistrationForm("Firstname", "Lastname",
				"e@mail.com", "123", "123", "1999-01-01", "Street",
				"1", "12345", "City", "DE123123123123", "MALADE61AKA");
		AccountEntity tenantAccountEnitity = accountManagement.createHostAccount(form);

		verify(userAccountManagement, times(1)) //
				.create(eq(form.getEmail()), //
						eq(Password.UnencryptedPassword.of(form.getPassword())), //
						eq(form.getEmail()), //
						eq(AccountManagement.HOST_ROLE));

		assertThat(tenantAccountEnitity.getAccount()).isNotNull();
	}

	@Test
	void createEventEmployeeAccount() {

		EventEmployeeRegistrationForm form = new EventEmployeeRegistrationForm("Firstname", "Lastname",
				"e@mail.com", "123", "123", "Company");
		AccountEntity tenantAccountEnitity = accountManagement.createEventEmployeeAccount(form);

		verify(userAccountManagement, times(1)) //
				.create(eq(form.getEmail()), //
						eq(Password.UnencryptedPassword.of(form.getPassword())), //
						eq(form.getEmail()), //
						eq(AccountManagement.EVENTEMPLOYEE_ROLE));

		assertThat(tenantAccountEnitity.getAccount()).isNotNull();
	}

	@Test
	void findByRole() {
	}

	@Test
	void findAllDisabled() {
	}

	@Test
	void enableTenant() {
	}

	@Test
	void deleteAccount() {
	}

	@Test
	void testDeleteAccount() {
	}

	@Test
	void getRepository() {
		assertThat(accountManagement.getRepository()).isEqualTo(accountRepository);
	}
}
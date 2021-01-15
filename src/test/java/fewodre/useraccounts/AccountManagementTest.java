package fewodre.useraccounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class AccountManagementTest {

	@BeforeEach
	void setUp() {
	}

	@Test
	void createTenantAccount() {

		AccountRepository accountRepository = mock(AccountRepository.class);
		when(accountRepository.save(any())).then(i -> i.getArgument(0));

		UserAccountManagement userAccountManager = mock(UserAccountManagement.class);
		UserAccount userAccount = mock(UserAccount.class);
		when(userAccountManager.create(any(), any(), any(), any())).thenReturn(userAccount);

		AccountManagement accountManagement = new AccountManagement(accountRepository, userAccountManager);

		TenantRegistrationForm form = new TenantRegistrationForm("Firstname", "Lastname",
				"e@mail.com", "123", "123", "1999-01-01", "Street",
				"1", "12345", "City", true);
		AccountEntity tenantAccountEnitity = accountManagement.createTenantAccount(form);

		verify(userAccountManager, times(1)) //
				.create(eq(form.getEmail()), //
						eq(Password.UnencryptedPassword.of(form.getPassword())), //
						eq(form.getEmail()), //
						eq(AccountManagement.UNACTIVATED_TENANT_ROLE));

		assertThat(tenantAccountEnitity.getAccount()).isNotNull();
	}

	@Test
	void createHostAccount() {
	}

	@Test
	void createEventEmployeeAccount() {
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
	}
}
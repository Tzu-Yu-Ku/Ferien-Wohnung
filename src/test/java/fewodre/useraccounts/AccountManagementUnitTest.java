package fewodre.useraccounts;

import fewodre.useraccounts.forms.EventEmployeeRegistrationForm;
import fewodre.useraccounts.forms.HostRegistrationForm;
import fewodre.useraccounts.forms.TenantRegistrationForm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.util.Streamable;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class AccountManagementUnitTest {

	private static AccountRepository accountRepository;
	private static AccountManagement accountManagement;
	private static UserAccountManagement userAccountManagement;
	private static LinkedList<UserAccount> userAccounts = new LinkedList<>();

	@BeforeAll
	static void beforeAll() {
		accountRepository = mock(AccountRepository.class);
		when(accountRepository.save(any())).then(i -> i.getArgument(0));

		userAccountManagement = mock(UserAccountManagement.class);

		UserAccount userAccount = mock(UserAccount.class);
		when(userAccountManagement.create(any(), any(), any(), any()))
				.thenAnswer((Answer) invocationOnMock -> {
					userAccounts.add(userAccount);
					return userAccount;
				})
				.thenReturn(userAccount);
		when(userAccountManagement.findAll()).thenReturn(Streamable.of(userAccounts));
		when(userAccountManagement.delete(userAccount))
				.thenAnswer((Answer) invocationOnMock -> {
					userAccounts.remove(userAccount);
					return userAccount;
				});
//		when(userAccountManagement.findByUsername(any())).thenAnswer((Answer) invocationOnMock -> userAccount);

		accountManagement = new AccountManagement(accountRepository, userAccountManagement);
	}


	@Test
	public void createTenantAccount() {

		TenantRegistrationForm form = new TenantRegistrationForm("Firstname", "Lastname",
				"e@mail.com", "123", "123", "1999-01-01", "Street",
				"1", "12345", "City", true);
		AccountEntity tenantAccountEnitity = accountManagement.createTenantAccount(form);

		verify(userAccountManagement, times(2)) //
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

		EventEmployeeRegistrationForm form = new EventEmployeeRegistrationForm("Firstname",
				"Lastname", "e@mail.com", "123", "123", "Company");
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
//		TenantRegistrationForm form = new TenantRegistrationForm("Firstname", "Lastname",
//				"e@mail.com", "123", "123", "1999-01-01", "Street",
//				"1", "12345", "City", true);
//		AccountEntity tenantAccountEnitity = accountManagement.createTenantAccount(form);
//		System.out.println(tenantAccountEnitity.getAccount().getEmail());
//		accountManagement.enableTenant("e@mail.com");
	}

	@Test
	void deleteAccount() {
		TenantRegistrationForm form = new TenantRegistrationForm("Firstname", "Lastname",
				"e@mail.com", "123", "123", "1999-01-01", "Street",
				"1", "12345", "City", true);
		AccountEntity tenantAccountEnitity = accountManagement.createTenantAccount(form);

		assertThat(tenantAccountEnitity.getAccount()).isNotNull();
		assertThat(userAccountManagement.findAll().isEmpty()).isFalse();

		accountManagement.deleteAccount(tenantAccountEnitity.getAccount());

		assertThat(userAccountManagement.findAll().isEmpty()).isTrue();
	}

	@Test
	void testDeleteAccount() {
	}

	@Test
	void getRepository() {
		assertThat(accountManagement.getRepository()).isEqualTo(accountRepository);
	}

}
package fewodre.useraccounts;

import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional
public class AccountManagement {

	public static final Role TENANT_ROLE = Role.of("TENANT");
	public static final Role HOST_ROLE = Role.of("HOST");
	public static final Role EVENTEMPLOYEE_ROLE = Role.of("EVENT_EMPLOYEE");
	public static final Role ADMIN_ROLE = Role.of("ADMIN");

	private final AccountRepository accounts;
	private final UserAccountManagement userAccounts;

	@Autowired
	AccountManagement(AccountRepository accounts, UserAccountManagement userAccounts) {

		Assert.notNull(accounts, "CustomerRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManagement must not be null!");

		this.accounts = accounts;
		this.userAccounts = userAccounts;
	}

//	public AccountEntity createTenantAccount(RegistrationForm registrationForm) {
//
//		Assert.notNull(registrationForm, "registrationForm should not be null!");
//
//		Password.UnencryptedPassword password = Password.UnencryptedPassword.of(registrationForm.getPassword());
//		UserAccount newUserAccount = userAccounts.create(registrationForm.getEmail(), password,
//				registrationForm.getEmail(), TENANT_ROLE);
//		newUserAccount.setFirstname(registrationForm.)
//		AccountEntity newAccount = new AccountEntity().
//
//		return accounts.save()
//	}

}

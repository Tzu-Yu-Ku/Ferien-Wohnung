package fewodre.useraccounts;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Set;

public interface AccountRepository extends CrudRepository<AccountEntity, Long> {

	AccountEntity findByUuid(String uuid);

	AccountEntity findByAccount_Email(String email);

	AccountEntity findByAccount(AccountEntity account);


}

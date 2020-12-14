package fewodre.useraccounts;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountEntity, Long> {

	AccountEntity findByUuid(String uuid);

	AccountEntity findByAccount_Email(String email);

	AccountEntity findByAccount(UserAccount account);

}

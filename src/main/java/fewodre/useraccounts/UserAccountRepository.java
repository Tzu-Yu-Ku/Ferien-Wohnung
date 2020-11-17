package fewodre.useraccounts;

import org.h2.engine.User;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

public interface UserAccountRepository extends CrudRepository<UserAccountEntity, Long> {

	UserAccountEntity findByEmail(String email);
	UserAccountEntity findByUuid(String uuid);
	UserAccountEntity findById(String id);

}

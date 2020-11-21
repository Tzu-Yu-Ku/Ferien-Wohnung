package fewodre.useraccounts;

import org.salespointframework.useraccount.UserAccountManagement;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountEntity, Long> {

	AccountEntity findByUuid(String uuid);
	AccountEntity findById(String id);
}

package fewodre.useraccounts;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountEntity, Long> {

	AccountEntity findByEmail(String email);
	AccountEntity findByUuid(String uuid);
	AccountEntity findById(String id);

}

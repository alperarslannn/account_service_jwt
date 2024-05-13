package account.domain.repositories;

import account.domain.Group;
import account.domain.UserAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

    @Override
    Optional<UserAccount> findById(Long id);
    Iterable<UserAccount> findAllByOrderByIdAsc();
    Optional<UserAccount> findFirstByAuthorities(Group role);
    Optional<UserAccount> findByEmailEqualsIgnoreCase(String email);
    void deleteUserAccountByEmail(String email);

}

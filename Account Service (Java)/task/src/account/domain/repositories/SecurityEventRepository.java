package account.domain.repositories;

import account.domain.SecurityEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface SecurityEventRepository extends CrudRepository<SecurityEvent, Long> {
    Collection<SecurityEvent> findAllByOrderByIdAsc();
}

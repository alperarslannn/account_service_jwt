package account.domain.repositories;

import account.domain.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e " +
            "JOIN e.userAccount u " +
            "WHERE e.period = :period " +
            "AND LOWER(u.email) = LOWER(:email) " +
            "ORDER BY e.period DESC")
    Optional<Employee> findByPeriodAndUserAccountEmailEqualsIgnoreCaseOrderByPeriodDesc(Date period, String email);
    List<Employee> findByUserAccountEmailEqualsIgnoreCaseOrderByPeriodDesc(String email);
    @Query("SELECT e FROM Employee e " +
            "JOIN e.userAccount u " +
            "WHERE LOWER(u.email) = LOWER(:email) " +
            "AND e.period = :period")
    Optional<Employee> findByUserAccountEmailEqualsIgnoreCaseAndPeriod(String email, Date period);
}

package account.domain;

import account.util.DateConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Date;

@Entity
@Table(name = "employee", uniqueConstraints = { @UniqueConstraint(columnNames = { "period", "user_account_id" }) })
public class Employee {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "EMPLOYEE_SEQ", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_account_id", nullable=false)
    private UserAccount userAccount;
    @Column
    @Convert(converter = DateConverter.class)
    private Date period;
    @Column
    private Long salaryInCent;

    public Long getId() {
        return id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Date getPeriod() {
        return period;
    }

    public void setPeriod(Date period) {
        this.period = period;
    }

    public Long getSalaryInCent() {
        return salaryInCent;
    }

    public void setSalaryInCent(Long salaryInCent) {
        this.salaryInCent = salaryInCent;
    }
}
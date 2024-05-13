package account.api.employee.dto;

import account.util.ValidMonthYear;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
@Validated
public class EmployeeUiDto {

    @NotEmpty(message = "Employee email cannot be empty!")
    private String employee;
    @JsonFormat(pattern="MM-yyyy")
    @ValidMonthYear
    private String period;
    @Positive(message = "Salary cannot be negative!")
    @JsonProperty(value = "salary")
    private Long salaryInCent;

    public String getEmployee() {
        return employee;
    }

    public String getPeriod() {
        return period;
    }

    public Long getSalaryInCent() {
        return salaryInCent;
    }
}

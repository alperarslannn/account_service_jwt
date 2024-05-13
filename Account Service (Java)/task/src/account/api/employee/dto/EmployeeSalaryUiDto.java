package account.api.employee.dto;

public class EmployeeSalaryUiDto {
    private String name;
    private String lastname;
    private String period;
    private String salary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(Long salaryInCent) {
        Long cents = salaryInCent % 100;
        Long dollars = salaryInCent / 100;
        this.salary = dollars + " dollar(s) " + cents + " cent(s)";
    }
}

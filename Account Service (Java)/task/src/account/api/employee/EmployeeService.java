package account.api.employee;

import account.api.employee.dto.EmployeeSalaryUiDto;
import account.api.employee.dto.EmployeeUiDto;
import account.domain.Employee;
import account.domain.UserAccount;
import account.domain.repositories.EmployeeRepository;
import account.domain.repositories.UserAccountRepository;
import account.exception.InvalidPeriodException;
import account.exception.UserDoesNotExistsException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service

public class EmployeeService {
    private final UserAccountRepository userAccountRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeService(UserAccountRepository userAccountRepository, EmployeeRepository employeeRepository) {
        this.userAccountRepository = userAccountRepository;
        this.employeeRepository = employeeRepository;
    }

    public void saveEmployeePayrolls(List<EmployeeUiDto> employeeUiDtoList){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");

        List<Employee> employeeList = new ArrayList<>();
        employeeUiDtoList.forEach(employeeUiDto -> {
            UserAccount userAccount = userAccountRepository.findByEmailEqualsIgnoreCase(employeeUiDto.getEmployee())
                    .orElseThrow(UserDoesNotExistsException::new);
            Employee employee = new Employee();
            employee.setUserAccount(userAccount);
            try {
                employee.setPeriod(sdf.parse(employeeUiDto.getPeriod()));
            } catch (ParseException e) {
                throw new InvalidPeriodException();
            }
            employee.setSalaryInCent(employeeUiDto.getSalaryInCent());
            employeeList.add(employee);
        });

        employeeRepository.saveAll(employeeList);
    }

    public void updateEmployeePayroll(EmployeeUiDto employeeUiDto){
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        Date period;
        try {
            period = sdf.parse(employeeUiDto.getPeriod());
        } catch (ParseException e) {
            throw new IllegalStateException("Cannot parse the date!");
        }

        Employee employee = employeeRepository.findByUserAccountEmailEqualsIgnoreCaseAndPeriod(employeeUiDto.getEmployee(), period).get();
        if (Objects.nonNull(employee)){
            employee.setSalaryInCent(employeeUiDto.getSalaryInCent());
        } else {
            UserAccount userAccount = userAccountRepository.findByEmailEqualsIgnoreCase(employeeUiDto.getEmployee())
                    .orElseThrow(UserDoesNotExistsException::new);
            employee = new Employee();
            employee.setUserAccount(userAccount);
            employee.setPeriod(period);
            employee.setSalaryInCent(employeeUiDto.getSalaryInCent());
        }

        employeeRepository.save(employee);
    }

    public EmployeeSalaryUiDto findEmployeePayroll(String period, String email) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
            sdf.setLenient(false);
            Date date = sdf.parse(period);
            Employee employee = employeeRepository.findByPeriodAndUserAccountEmailEqualsIgnoreCaseOrderByPeriodDesc(date, email).orElseThrow();
            EmployeeSalaryUiDto employeeSalaryUiDto = new EmployeeSalaryUiDto();
            employeeSalaryUiDto.setName(employee.getUserAccount().getName());
            employeeSalaryUiDto.setLastname(employee.getUserAccount().getLastname());
            employeeSalaryUiDto.setPeriod(new SimpleDateFormat("MMMM-yyyy", Locale.ENGLISH).format(employee.getPeriod()));
            employeeSalaryUiDto.setSalary(employee.getSalaryInCent());
            return employeeSalaryUiDto;
        } catch (ParseException e) {
            throw new InvalidPeriodException();
        }

    }

    public List<EmployeeSalaryUiDto> findEmployeeAllPayrolls(String email) {

        List<Employee> employeeList = employeeRepository.findByUserAccountEmailEqualsIgnoreCaseOrderByPeriodDesc(email);
        List<EmployeeSalaryUiDto> employeeSalaryUiDtoList = new ArrayList<>();

        employeeList.forEach(employee -> {
            EmployeeSalaryUiDto employeeSalaryUiDto = new EmployeeSalaryUiDto();
            employeeSalaryUiDto.setName(employee.getUserAccount().getName());
            employeeSalaryUiDto.setLastname(employee.getUserAccount().getLastname());
            employeeSalaryUiDto.setPeriod(new SimpleDateFormat("MMMM-yyyy", Locale.ENGLISH).format(employee.getPeriod()));
            employeeSalaryUiDto.setSalary(employee.getSalaryInCent());
            employeeSalaryUiDtoList.add(employeeSalaryUiDto);
        });

        return employeeSalaryUiDtoList;

    }
}

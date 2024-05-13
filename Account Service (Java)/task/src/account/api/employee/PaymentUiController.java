package account.api.employee;

import account.api.employee.dto.EmployeeUiDto;
import account.api.employee.dto.SuccessUiDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping(value="/api/acct")
public class PaymentUiController {
    private final EmployeeService employeeService;

    public PaymentUiController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(value="/payments")
    public ResponseEntity<SuccessUiDto> uploadPayrolls(@Valid @RequestBody List<EmployeeUiDto> employeeUiDtoList){
        employeeService.saveEmployeePayrolls(employeeUiDtoList);
        return ResponseEntity.ok(new SuccessUiDto("Added successfully!"));
    }

    @PutMapping(value="/payments")
    public ResponseEntity<SuccessUiDto> changeSalaryOfEmployee(@Valid @RequestBody EmployeeUiDto employeeUiDto){
        employeeService.updateEmployeePayroll(employeeUiDto);
        return ResponseEntity.ok(new SuccessUiDto("Updated successfully!"));
    }
}
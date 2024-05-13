package account.api.employee;

import account.api.security.CustomUserDetails;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping(value="/api/empl")
@Validated
public class EmployeeUiController {
    private final EmployeeService employeeService;


    public EmployeeUiController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping(value="/payment")
    public ResponseEntity<?> getEmployeePayroll(Authentication authentication, @RequestParam(required = false, name = "period") @Pattern(regexp = "\\d{2}-\\d{4}", message = "Period must be in MM-yyyy format") String period){
        if (Objects.nonNull(period)){
            return ResponseEntity.ok(employeeService.findEmployeePayroll(period, ((CustomUserDetails) authentication.getPrincipal()).getUsername()));
        } else {
            return ResponseEntity.ok(employeeService.findEmployeeAllPayrolls(((CustomUserDetails) authentication.getPrincipal()).getUsername()));
        }
    }
}

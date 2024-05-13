package account.api.admin;

import account.api.SecurityEventResponseEntity;
import account.api.admin.dto.UserLockUiDto;
import account.api.admin.dto.UserRoleUiDto;
import account.api.employee.dto.SuccessUiDto;
import account.api.security.UserAccountService;
import account.api.security.dto.UserUiDto;
import account.api.security.event.LogSecurityEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static account.api.security.event.SecurityEventType.DELETE_USER;
import static account.api.security.event.SecurityEventType.GRANT_ROLE;
import static account.api.security.event.SecurityEventType.LOCK_USER;
import static account.api.security.event.SecurityEventType.REMOVE_ROLE;
import static account.api.security.event.SecurityEventType.UNLOCK_USER;

@RestController
@RequestMapping(value="/api/admin/user")
public class AdminUiController {
    private final UserAccountService userAccountService;

    public AdminUiController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping(value="/")
    public ResponseEntity<List<UserUiDto>> getAllUserAccountInformation(){
        return ResponseEntity.ok(userAccountService.findAllUsers());
    }

    @PutMapping(value="/role")
    @LogSecurityEvent
    public ResponseEntity<UserUiDto> changeUserRole(@RequestBody UserRoleUiDto userRoleUiDto){
        UserUiDto userUiDto = userAccountService.setUserAccountRoles(userRoleUiDto);
        String toOrFrom = userRoleUiDto.getOperation().name().equals(UserRoleUiDto.OperationType.GRANT.name()) ? "to":"from";
        return new SecurityEventResponseEntity.Builder<>(userUiDto, HttpStatus.OK)
                .eventName(userRoleUiDto.getOperation().name().equals(UserRoleUiDto.OperationType.GRANT.name()) ? GRANT_ROLE:REMOVE_ROLE)
                .path("/api/admin/user/role")
                .date(Date.from(Instant.now()))
                .objectAccountId(userUiDto.getId())
                .object(userRoleUiDto.getOperation().name().substring(0, 1).toUpperCase() + userRoleUiDto.getOperation().name().substring(1).toLowerCase() + " role " + userRoleUiDto.getRole() + " " + toOrFrom + " " + userUiDto.getEmail())
                .build();
    }

    @DeleteMapping(value="/{email}")
    @LogSecurityEvent
    public ResponseEntity<SuccessUiDto> deleteUserAccount(Authentication authentication, @PathVariable String email){
        SuccessUiDto successUiDto = userAccountService.deleteUserAccount(email, authentication);
        return new SecurityEventResponseEntity.Builder<>(successUiDto, HttpStatus.OK)
                .eventName(DELETE_USER)
                .path("/api/admin/user")
                .date(Date.from(Instant.now()))
                .objectAccountId(successUiDto.getId())
                .object(email)
                .build();
    }

    @PostMapping(value="/access")
    @LogSecurityEvent
    public ResponseEntity<SuccessUiDto> userAccountLockSwitch(@RequestBody UserLockUiDto userLockUiDto){
        SuccessUiDto successUiDto = userAccountService.accountLocking(userLockUiDto);
        return new SecurityEventResponseEntity.Builder<>(successUiDto, HttpStatus.OK)
                .eventName(userLockUiDto.getOperation().name().equals(UserLockUiDto.OperationType.LOCK.name()) ? LOCK_USER:UNLOCK_USER)
                .path("/api/admin/user/access")
                .date(Date.from(Instant.now()))
                .objectAccountId(successUiDto.getId())
                .object(userLockUiDto.getOperation().name().substring(0, 1).toUpperCase() + userLockUiDto.getOperation().name().substring(1).toLowerCase() + " user " + userLockUiDto.getUser())
                .build();
    }

    @PutMapping(value="/access")
    @LogSecurityEvent
    public ResponseEntity<SuccessUiDto> userAccountLockUpdate(@RequestBody UserLockUiDto userLockUiDto){
        SuccessUiDto successUiDto = userAccountService.updateAccountLocking(userLockUiDto);
        return new SecurityEventResponseEntity.Builder<>(successUiDto, HttpStatus.OK)
                .eventName(userLockUiDto.getOperation().name().equals(UserLockUiDto.OperationType.LOCK.name()) ? LOCK_USER:UNLOCK_USER)
                .path("/api/admin/user/access")
                .date(Date.from(Instant.now()))
                .objectAccountId(successUiDto.getId())
                .object(userLockUiDto.getOperation().name().substring(0, 1).toUpperCase() + userLockUiDto.getOperation().name().substring(1).toLowerCase() + " user " + userLockUiDto.getUser().toLowerCase())
                .build();
    }


}
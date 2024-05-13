package account.api.security;

import account.api.SecurityEventResponseEntity;
import account.api.security.dto.NewPasswordUiDto;
import account.api.security.dto.PasswordUpdatedUiDto;
import account.api.security.dto.SignupUiDto;
import account.api.security.dto.UserUiDto;
import account.api.security.event.LogSecurityEvent;
import account.domain.UserAccount;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;

import static account.api.security.event.SecurityEventType.CHANGE_PASSWORD;
import static account.api.security.event.SecurityEventType.CREATE_USER;

@RestController
@RequestMapping(value="/api/auth")
public class AuthUiController {
    private final UserAccountService userAccountService;

    public AuthUiController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping(value="/signup")
    @LogSecurityEvent
    public ResponseEntity<UserUiDto> signup(@Validated @RequestBody SignupUiDto signupUiDto){
        UserUiDto userUiDto = userAccountService.addUser(signupUiDto);
        return new SecurityEventResponseEntity.Builder<>(userUiDto, HttpStatus.OK)
                .eventName(CREATE_USER)
                .path("/api/auth/signup")
                .date(Date.from(Instant.now()))
                .objectAccountId(userUiDto.getId())
                .object(userUiDto.getEmail())
                .build();
    }

    @GetMapping(value="/signin")
    public ResponseEntity<UserUiDto> signin(Authentication authentication){
        UserAccount userAccount = userAccountService.findByUsername(((CustomUserDetails) authentication.getPrincipal()).getUsername());
        return ResponseEntity.ok(new UserUiDto(userAccount.getId(), userAccount.getName(), userAccount.getLastname(), userAccount.getEmail(), userAccount.getRolesAsString()));
    }

    @PostMapping(value="/changepass")
    @LogSecurityEvent
    public ResponseEntity<PasswordUpdatedUiDto> changePassword(@Validated @RequestBody NewPasswordUiDto newPasswordUiDto, Authentication authentication){
        return new SecurityEventResponseEntity.Builder<>(userAccountService.updatePassword(newPasswordUiDto, authentication), HttpStatus.OK)
                .eventName(CHANGE_PASSWORD)
                .path("/api/auth/changepass")
                .date(Date.from(Instant.now()))
                .objectAccountId(((CustomUserDetails) authentication.getPrincipal()).getId())
                .object(((CustomUserDetails) authentication.getPrincipal()).getUsername())
                .build();
    }
}

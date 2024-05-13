package account.api.security;

import account.domain.UserAccount;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {

    private final UserAccountService userAccountService;

    public AuthenticationSuccessListener(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        CustomUserDetails userDetails =  (CustomUserDetails) event.getAuthentication().getPrincipal();
        UserAccount userAccount = userAccountService.findByUsername(userDetails.getUsername());
        if (userAccount.getFailedAttempt() > 0) {
            userAccountService.resetFailedAttempCount(userAccount);
        }
    }
}
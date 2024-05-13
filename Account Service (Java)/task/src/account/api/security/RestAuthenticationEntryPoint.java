package account.api.security;

import account.domain.SecurityEvent;
import account.domain.UserAccount;
import account.domain.repositories.SecurityEventRepository;
import account.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

import static account.api.security.event.SecurityEventType.BRUTE_FORCE;
import static account.api.security.event.SecurityEventType.LOCK_USER;
import static account.api.security.event.SecurityEventType.LOGIN_FAILED;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    public static final String JAKARTA_SERVLET_ERROR_REQUEST_URI = "jakarta.servlet.error.request_uri";
    private static final int MAX_FAILED_ATTEMPT_COUNT = 5;
    private final SecurityEventRepository securityEventRepository;
    private final UserAccountService userAccountService;

    public RestAuthenticationEntryPoint(SecurityEventRepository securityEventRepository, UserAccountService userAccountService) {
        this.securityEventRepository = securityEventRepository;
        this.userAccountService = userAccountService;
    }

    @Override
    @Transactional
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        String header = request.getHeader("Authorization");
        String email = findEmail(header);

        try {
            UserAccount userAccount = userAccountService.findByUsername(email);
            if(userAccount.isLocked()){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("{");
                response.getWriter().println("  \"timestamp\" : \""+ LocalDateTime.now() +"\",");
                response.getWriter().println("  \"status\" :" + HttpServletResponse.SC_UNAUTHORIZED + ",");
                response.getWriter().println("  \"error\" : \"Unauthorized\",");
                response.getWriter().println("  \"message\" : \"User account is locked\",");
                response.getWriter().println("  \"path\" : \""+ request.getAttribute(JAKARTA_SERVLET_ERROR_REQUEST_URI)+"\"");
                response.getWriter().println("}");
                return;
            } else {
                userAccountService.increaseFailedAttempCount(userAccount);
                saveLoginFailedSecurityEvent(request, userAccount);
                if (userAccount.getFailedAttempt() >= MAX_FAILED_ATTEMPT_COUNT) {
                    saveBruteForceSecurityEvent(request, userAccount);
                    if(!userAccount.getRoles().contains(Role.ADMINISTRATOR)) userAccount.setLocked(true);
                    saveLockUserSecurityEvent(request, userAccount);
                    userAccountService.saveUser(userAccount);
                }
            }
        } catch (UserNotFoundException e) {
            if(!email.isEmpty()) saveLoginFailedSecurityEventForAnonymousUser(request, email);
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

    private String findEmail(String header) {
        String email = "";
        if (header != null && header.startsWith("Basic ")) {
            String[] credentials = extractAndDecodeHeader(header);
            if (credentials.length == 2) {
                email = credentials[0];
            }
        }
        return email;
    }

    private String[] extractAndDecodeHeader(String header) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new IllegalStateException("Invalid basic authentication token");
        }

        return new String[] { token.substring(0, delim), token.substring(delim + 1) };
    }

    private void saveBruteForceSecurityEvent(HttpServletRequest request, UserAccount userAccount) {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setEventName(BRUTE_FORCE);
        securityEvent.setPath((String) request.getAttribute(JAKARTA_SERVLET_ERROR_REQUEST_URI));
        securityEvent.setDate(Date.from(Instant.now()));
        securityEvent.setSubjectAccountId(userAccount.getId());
        securityEvent.setObjectAccountId(userAccount.getId());
        securityEvent.setObject((String) request.getAttribute(JAKARTA_SERVLET_ERROR_REQUEST_URI));
        securityEventRepository.save(securityEvent);
    }

    private void saveLockUserSecurityEvent(HttpServletRequest request, UserAccount userAccount) {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setEventName(LOCK_USER);
        securityEvent.setPath((String) request.getAttribute(JAKARTA_SERVLET_ERROR_REQUEST_URI));
        securityEvent.setDate(Date.from(Instant.now()));
        securityEvent.setSubjectAccountId(userAccount.getId());
        securityEvent.setObjectAccountId(userAccount.getId());
        securityEvent.setObject("Lock user " + userAccount.getEmail());
        securityEventRepository.save(securityEvent);
    }

    private void saveLoginFailedSecurityEvent(HttpServletRequest request, UserAccount userAccount) {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setEventName(LOGIN_FAILED);
        securityEvent.setPath((String) request.getAttribute(JAKARTA_SERVLET_ERROR_REQUEST_URI));
        securityEvent.setDate(Date.from(Instant.now()));
        securityEvent.setSubjectAccountId(userAccount.getId());
        securityEvent.setObjectAccountId(userAccount.getId());
        securityEvent.setObject(userAccount.getEmail());
        securityEventRepository.save(securityEvent);
    }

    private void saveLoginFailedSecurityEventForAnonymousUser(HttpServletRequest request, String email) {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setEventName(LOGIN_FAILED);
        securityEvent.setPath((String) request.getAttribute(JAKARTA_SERVLET_ERROR_REQUEST_URI));
        securityEvent.setDate(Date.from(Instant.now()));
        securityEvent.setSubjectAccountId(0L);
        securityEvent.setObjectAccountId(0L);
        securityEvent.setObject(email);
        securityEventRepository.save(securityEvent);
    }

}

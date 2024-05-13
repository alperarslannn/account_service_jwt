package account.api.security;

import account.domain.SecurityEvent;
import account.domain.repositories.SecurityEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static account.api.security.event.SecurityEventType.ACCESS_DENIED;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final SecurityEventRepository securityEventRepository;

    public CustomAccessDeniedHandler(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        saveSecurityEvent(request);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println("{");
        writer.println("\"timestamp\" : \""+ LocalDateTime.now() +"\",");
        writer.println("\"status\" :" + HttpServletResponse.SC_FORBIDDEN + ",");
        writer.println("\"error\" : \"Forbidden\",");
        writer.println("\"message\" : \"Access Denied!\",");
        writer.println("\"path\" : \""+request.getServletPath()+"\"");
        writer.println("}");
    }

    private void saveSecurityEvent(HttpServletRequest request) {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setEventName(ACCESS_DENIED);
        securityEvent.setPath(request.getServletPath());
        securityEvent.setDate(Date.from(Instant.now()));
        securityEvent.setSubjectAccountId(((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()); //
        securityEvent.setObjectAccountId(((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()); //
        securityEvent.setObject(request.getServletPath());
        securityEventRepository.save(securityEvent);
    }
}

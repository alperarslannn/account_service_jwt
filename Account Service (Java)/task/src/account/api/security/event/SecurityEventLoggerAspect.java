package account.api.security.event;

import account.api.SecurityEventResponseEntity;
import account.domain.SecurityEvent;
import account.domain.repositories.SecurityEventRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SecurityEventLoggerAspect {

    private SecurityEventRepository securityEventRepository;

    public SecurityEventLoggerAspect(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    @AfterReturning(pointcut = "@annotation(logSecurityEvent)", returning = "response")
    public void logSecurityEvent(LogSecurityEvent logSecurityEvent, SecurityEventResponseEntity<?> response) {
        if (response != null) {
            SecurityEvent securityEvent = new SecurityEvent();
            securityEvent.setEventName(response.getEventName());
            securityEvent.setPath(response.getPath());
            securityEvent.setDate(response.getDate());
            securityEvent.setSubjectAccountId(response.getSubjectAccountId());
            securityEvent.setObjectAccountId(response.getObjectAccountId());
            securityEvent.setObject(response.getObject());
            securityEventRepository.save(securityEvent);
        }
    }
}

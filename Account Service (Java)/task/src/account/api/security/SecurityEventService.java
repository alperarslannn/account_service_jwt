package account.api.security;

import account.api.security.dto.SecurityEventUiDto;
import account.api.security.event.SecurityEventType;
import account.domain.SecurityEvent;
import account.domain.UserAccount;
import account.domain.repositories.SecurityEventRepository;
import account.domain.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SecurityEventService {
    private final SecurityEventRepository securityEventRepository;
    private final UserAccountRepository userAccountRepository;

    public SecurityEventService(SecurityEventRepository securityEventRepository, UserAccountRepository userAccountRepository) {
        this.securityEventRepository = securityEventRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public List<SecurityEventUiDto> findAllSecurityEvents(){
        Collection<SecurityEvent> securityEventList = securityEventRepository.findAllByOrderByIdAsc();
        if(securityEventList.isEmpty()) return new ArrayList<>();
        return securityEventList.stream()
                .map(securityEvent -> {
                   UserAccount subjectUserAccount = userAccountRepository.findById(securityEvent.getSubjectAccountId()).orElse(null);
                   if(securityEvent.getEventName().equals(SecurityEventType.LOGIN_FAILED)){
                       return new SecurityEventUiDto(
                               securityEvent.getDate(),
                               securityEvent.getEventName().name(),
                               securityEvent.getObject(),
                               securityEvent.getPath(),
                               securityEvent.getPath());
                   } else {
                       return new SecurityEventUiDto(
                            securityEvent.getDate(),
                            securityEvent.getEventName().name(),
                            subjectUserAccount != null ? subjectUserAccount.getEmail():"Anonymous",
                            securityEvent.getObject(),
                            securityEvent.getPath());
                   }
                }).toList();

    }
}

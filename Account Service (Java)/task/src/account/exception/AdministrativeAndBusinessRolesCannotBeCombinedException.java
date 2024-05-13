package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The user cannot combine administrative and business roles!")
public class AdministrativeAndBusinessRolesCannotBeCombinedException extends BaseException {
    private final LocalDateTime timestamp;

    public AdministrativeAndBusinessRolesCannotBeCombinedException() {
        super();
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
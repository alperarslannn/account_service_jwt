package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Role not found!")
public class RoleNotFoundException extends BaseException {
    private final LocalDateTime timestamp = LocalDateTime.now();

    public RoleNotFoundException() {
        super();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}

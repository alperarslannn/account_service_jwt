package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The user must have at least one role!")
public class UserHasOnlyOneRoleException extends BaseException {
    private final LocalDateTime timestamp;

    public UserHasOnlyOneRoleException() {
        super();
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
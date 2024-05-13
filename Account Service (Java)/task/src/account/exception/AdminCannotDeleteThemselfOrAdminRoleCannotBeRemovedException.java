package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Can't remove ADMINISTRATOR role!")
public class AdminCannotDeleteThemselfOrAdminRoleCannotBeRemovedException extends BaseException {
    private final LocalDateTime timestamp;

    public AdminCannotDeleteThemselfOrAdminRoleCannotBeRemovedException() {
        super();
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}

package account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;


@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BaseException extends RuntimeException {
    private final LocalDateTime timestamp;

    public BaseException() {
        super();
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}

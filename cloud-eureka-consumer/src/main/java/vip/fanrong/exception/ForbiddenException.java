package vip.fanrong.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "do not have permission")
public class ForbiddenException extends Exception {
    public ForbiddenException(String message) {
        super(message);
    }
}

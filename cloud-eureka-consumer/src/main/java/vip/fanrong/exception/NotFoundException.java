package vip.fanrong.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)//找不到页面
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}

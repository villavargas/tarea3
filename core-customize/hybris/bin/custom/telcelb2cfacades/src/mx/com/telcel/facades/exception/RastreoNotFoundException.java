package mx.com.telcel.facades.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RastreoNotFoundException extends RuntimeException {


    public RastreoNotFoundException() {
        this("Not Found");
    }

    public RastreoNotFoundException(String message) {
        super(message);
    }

    public RastreoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}

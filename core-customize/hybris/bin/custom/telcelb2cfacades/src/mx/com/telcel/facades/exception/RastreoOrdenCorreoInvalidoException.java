package mx.com.telcel.facades.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RastreoOrdenCorreoInvalidoException extends RuntimeException {

    public RastreoOrdenCorreoInvalidoException() {
        this("Correo invalido");
    }

    public RastreoOrdenCorreoInvalidoException(String message) {
        super(message);
    }

    public RastreoOrdenCorreoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }

}

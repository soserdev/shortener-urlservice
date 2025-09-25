package dev.smo.shortener.urlservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UrlServiceException extends RuntimeException {

    public UrlServiceException(String message) {
        super(message);
    }

}

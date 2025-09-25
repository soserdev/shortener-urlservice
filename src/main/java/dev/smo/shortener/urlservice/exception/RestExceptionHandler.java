package dev.smo.shortener.urlservice.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(value=HttpStatus.UNPROCESSABLE_ENTITY, reason="Duplicate shortUrl!")
    @ExceptionHandler(DuplicateKeyException.class)
    public void handleDuplicateKeyException(){
    }
}

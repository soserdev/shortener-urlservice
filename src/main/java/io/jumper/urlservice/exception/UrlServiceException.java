package io.jumper.urlservice.exception;

public class UrlServiceException extends RuntimeException {

    public UrlServiceException() {
    }

    public UrlServiceException(String message) {
        super(message);
    }

    public UrlServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UrlServiceException(Throwable cause) {
        super(cause);
    }

    public UrlServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

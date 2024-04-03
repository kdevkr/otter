package kr.kdev.otter.security.exception;

import org.springframework.security.authentication.InsufficientAuthenticationException;

public class InvalidBearerTokenException extends InsufficientAuthenticationException {
    public InvalidBearerTokenException(String msg) {
        super(msg);
    }

    public InvalidBearerTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

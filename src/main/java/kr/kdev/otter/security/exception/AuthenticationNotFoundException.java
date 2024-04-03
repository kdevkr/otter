package kr.kdev.otter.security.exception;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class AuthenticationNotFoundException extends AuthenticationCredentialsNotFoundException {

    public AuthenticationNotFoundException(String msg) {
        super(msg);
    }

    public AuthenticationNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

package katopia.fitcheck.global.exception;

import katopia.fitcheck.global.exception.code.ResponseCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AuthException extends AuthenticationException {
    private final ResponseCode errorCode;

    public AuthException(ResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
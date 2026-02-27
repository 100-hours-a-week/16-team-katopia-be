package katopia.fitcheck.global.exception;

import jakarta.validation.ConstraintViolation;
import katopia.fitcheck.global.APIResponse;
import katopia.fitcheck.global.exception.code.AuthErrorCode;
import katopia.fitcheck.global.exception.code.CommentErrorCode;
import katopia.fitcheck.global.exception.code.CommonErrorCode;
import katopia.fitcheck.global.exception.code.MemberErrorCode;
import katopia.fitcheck.global.exception.code.PostErrorCode;
import katopia.fitcheck.global.exception.code.ResponseCode;
import katopia.fitcheck.global.exception.code.VoteErrorCode;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<String, ResponseCode> CODE_MAP = new HashMap<>();

    static {
        registerCodes(CommonErrorCode.values());
        registerCodes(MemberErrorCode.values());
        registerCodes(PostErrorCode.values());
        registerCodes(CommentErrorCode.values());
        registerCodes(AuthErrorCode.values());
        registerCodes(VoteErrorCode.values());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIResponse<?>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return APIResponse.error(CommonErrorCode.API_NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<APIResponse<?>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.warn("Method not supported: {}", ex.getMethod());
        return APIResponse.error(CommonErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<APIResponse<?>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {}", ex.getErrorCode().getMessage());
        return APIResponse.error(ex.getErrorCode());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<APIResponse<?>> handleAuthException(AuthException ex) {
        log.warn("Auth exception: {}", ex.getErrorCode().getMessage());
        return APIResponse.error(ex.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(null);
        return buildValidationResponse(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Request body parse error", ex);
        return APIResponse.error(CommonErrorCode.INVALID_INPUT_VALUE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIResponse<?>> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse(null);
        return buildValidationResponse(message);
    }

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handleAsyncRequestNotUsable(AsyncRequestNotUsableException ex) {
        log.debug("Async request closed by client: {}", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<?>> handleUnhandled(Exception ex) {
        log.error("Unhandled exception", ex);
        return APIResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<APIResponse<?>> buildValidationResponse(String message) {
        if (message == null || message.isBlank()) {
            return APIResponse.error(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        String requiredPrefix = CommonErrorCode.REQUIRED_VALUE.getCode() + ":";
        if (message.startsWith(requiredPrefix)) {
            String field = message.substring(requiredPrefix.length());
            String formatted = CommonErrorCode.REQUIRED_VALUE.getFormattedMessage(field);
            return APIResponse.error(CommonErrorCode.REQUIRED_VALUE, formatted);
        }
        ResponseCode code = CODE_MAP.get(message);
        if (code == null) {
            return APIResponse.error(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        return APIResponse.error(code);
    }

    private static void registerCodes(ResponseCode[] codes) {
        for (ResponseCode code : codes) {
            CODE_MAP.put(code.getCode(), code);
        }
    }
}

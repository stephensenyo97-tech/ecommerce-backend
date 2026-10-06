package com.example.ecommerce.common.exception;

import com.example.ecommerce.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.stream.Collectors;


@RestControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                        HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }
    //taste exception added
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }
//taste
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedActionException(UnauthorizedActionException ex,HttpServletRequest request){
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }
    //taste
@ExceptionHandler(DuplicateUsernameException.class)
public ResponseEntity<ApiErrorResponse> handleDuplicateUsernameException (DuplicateUsernameException ex, HttpServletRequest request){
        return build(HttpStatus.CONFLICT, ex.getMessage(),request);
}

@ExceptionHandler(DuplicateEmailException.class)
public ResponseEntity<ApiErrorResponse> handleDuplicateEmailException (DuplicateEmailException ex, HttpServletRequest request){
        return build(HttpStatus.CONFLICT,ex.getMessage(),request);
}
@ExceptionHandler(SamePasswordException.class)
public ResponseEntity<ApiErrorResponse> handleSamePasswordException(SamePasswordException ex,HttpServletRequest request){
        return build(HttpStatus.BAD_REQUEST,ex.getMessage(),request);
}

@ExceptionHandler(DuplicateRequestException.class)
public ResponseEntity<ApiErrorResponse> handleDuplicateRequestException (DuplicateRequestException ex, HttpServletRequest request){

        return build(HttpStatus.CONFLICT,ex.getMessage(),request);
}

@ExceptionHandler(RequestNotFoundException.class)
public ResponseEntity<ApiErrorResponse> handleRequestNotFoundException(RequestNotFoundException ex,HttpServletRequest request){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
}

@ExceptionHandler(RequestDeniedException.class)
public ResponseEntity<ApiErrorResponse> handleRequestDeniedException(RequestDeniedException ex, HttpServletRequest request){
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
}

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request){
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }


    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequestException(InvalidRequestException ex, HttpServletRequest request){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }


    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

}

package ru.flint.voteforlunch.web;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.flint.voteforlunch.exceptions.ApplicationException;
import ru.flint.voteforlunch.exceptions.VoteTimeConstraintException;
import ru.flint.voteforlunch.util.ValidationUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;

    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception
            , HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail body = exception.updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale());
        Map<String, String> invalidParams = new LinkedHashMap<>();
        for (ObjectError error : exception.getBindingResult().getGlobalErrors()) {
            invalidParams.put(error.getObjectName(), getErrorMessage(error));
        }
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            invalidParams.put(error.getField(), getErrorMessage(error));
        }
        body.setProperty("invalid_params", invalidParams);
        body.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        return handleExceptionInternal(exception, body, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> appException(ApplicationException exception, WebRequest request) {
        log.error("ApplicationException: {}", exception.getMessage());
        return createProblemDetailExceptionResponse(exception, exception.getStatusCode(), request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<?> entityNotFoundException(WebRequest request, EntityNotFoundException exception) {
        log.error("EntityNotFoundException: {}", exception.getMessage());
        return createProblemDetailExceptionResponse(exception, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(VoteTimeConstraintException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<?> voteTimeConstraintException(WebRequest request, VoteTimeConstraintException exception) {
        log.error("VoteTimeConstraintException: {}", exception.getMessage());
        return createProblemDetailExceptionResponse(exception, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(WebRequest request
            , ConstraintViolationException exception) {
        ProblemDetail body = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        Map<String, String> invalidParams = new LinkedHashMap<>();
        for (ConstraintViolation<?> constraintViolation : exception.getConstraintViolations()) {
            invalidParams.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        }
        body.setProperty("invalid_params", invalidParams);
        return handleExceptionInternal(exception, body, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> conflict(WebRequest request, DataIntegrityViolationException exception) {
        log.error("DataIntegrityViolationException: {}", exception.getMessage());
        String message = null;
        if (ValidationUtil.getRootCause(exception).getMessage().toLowerCase().contains("email_unique_idx")) {
            message = "User with this email already exists";
        }
        if (ValidationUtil.getRootCause(exception).getMessage().toLowerCase().contains("uc_menu_date_of_menu")) {
            message = "Menu for this restaurant on this date already exists";
        }
        if (ValidationUtil.getRootCause(exception).getMessage().toLowerCase().contains("foreign key(restaurant_id)")) {
            message = "Wrong id for restaurant";
        }
        return createProblemDetailExceptionResponse(exception, HttpStatus.CONFLICT, request, message);
    }

    private ResponseEntity<?> createProblemDetailExceptionResponse(Exception ex, HttpStatusCode statusCode
            , WebRequest request) {
        return createProblemDetailExceptionResponse(ex, statusCode, request, null);
    }

    private ResponseEntity<?> createProblemDetailExceptionResponse(Exception ex, HttpStatusCode statusCode
            , WebRequest request, @Nullable String customMessage) {
        String msg = customMessage != null ? customMessage : ex.getMessage();
        ProblemDetail body = createProblemDetail(ex, statusCode, msg, null
                , null, request);
        return handleExceptionInternal(ex, body, new HttpHeaders(), statusCode, request);
    }

    private String getErrorMessage(ObjectError error) {
        return messageSource.getMessage(Objects.requireNonNull(error.getCode()), error.getArguments()
                , error.getDefaultMessage(), LocaleContextHolder.getLocale());
    }
}

package com.stevenhampton.addressbook.controller;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.logging.LoggingHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

/**
 * Advice for handling exceptions thrown by the application.
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationControllerAdvice {
    private final @NonNull LoggingHelper loggingHelper;

    /**
     * Log the given {@link Throwable} and return its message.
     * @param ex {@link Throwable} to handle
     * @return the throwable's message
     */
    private String logThrowable(Throwable ex) {
        LoggingHelper.logThrowable(ex, log, "{}caught Throwable", loggingHelper.getCurrentUsernameAsLoggingPrefix());
        return ex.getLocalizedMessage();
    }

    /**
     * Handle any {@link DataIntegrityException}, {@link ConstraintViolationException} or {@link
     * EmptyResultDataAccessException} by setting a status of BAD_REQUEST and using the exception's message as the body
     * content (prevents stack traces from being exposed externally).
     * @param ex {@link DataIntegrityException} to handle
     * @return the exception's message
     */
    @ResponseBody
    @ExceptionHandler(
            {DataIntegrityException.class, ConstraintViolationException.class, EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String dataExceptionHandler(Exception ex) {
        return logThrowable(ex);
    }

    /**
     * Handle any other unexpected {@link Throwable}s in a tidy way, setting the status to INTERNAL_SERVER_ERROR.
     * @param throwable {@link Throwable} to handle gracefully
     * @return message from {@link Throwable}
     */
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String handleUnexpectedErrors(Throwable throwable) {
        return logThrowable(throwable);
    }
}

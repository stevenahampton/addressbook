package com.stevenhampton.addressbook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * Utility methods for controller classes.
 */
public class ControllerUtil {
    private ControllerUtil() {
    }

    /**
     * Get a meaningful status for the given response body.
     * @param body any response body data but {@link Optional} and {@link Iterable} will be checked to determine if
     *             NOT_FOUND should be returned
     * @param <T>  type of response body
     * @return an {@link HttpStatus}
     */
    public static <T> HttpStatus getStatus(T body) {
        return body instanceof Optional<?> optional && optional.isEmpty()
                || body instanceof Iterable<?> iterable && !iterable.iterator().hasNext() ? HttpStatus.NOT_FOUND
                : HttpStatus.OK;
    }

    /**
     * Convert an {@link Optional} into a {@link ResponseEntity}.
     * @param body contents of {@link Optional}
     * @param <T>  type of contents
     * @return a matching {@link ResponseEntity}
     */
    public static <T> ResponseEntity<T> getResponseEntityFromOptional(Optional<T> body) {
        return ResponseEntity.status(ControllerUtil.getStatus(body)).body(body.orElse(null));
    }

    /**
     * Convert any response body into a {@link ResponseEntity}.
     * @param body body for response
     * @param <T>  type of body
     * @return a matching {@link ResponseEntity}
     */
    public static <T> ResponseEntity<T> getResponseEntity(T body) {
        return ResponseEntity.status(ControllerUtil.getStatus(body)).body(body);
    }
}

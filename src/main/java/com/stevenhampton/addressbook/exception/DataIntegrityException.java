package com.stevenhampton.addressbook.exception;

import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Exception thrown when request data is incorrect or invalid.
 */
public class DataIntegrityException extends Exception {
    public DataIntegrityException(String message) {
        super(message);
    }

    /**
     * Create a {@link DataIntegrityException} using a message retrieved from the given {@link MessageSource}.
     * @param messageSource {@link MessageSource} from which to retrieve the message
     * @param key           message key in {@link MessageSource}
     * @param args          any arguments for the message
     */
    public DataIntegrityException(MessageSource messageSource, String key, Object... args) {
        super(messageSource.getMessage(key, args, Locale.getDefault()));
    }
}

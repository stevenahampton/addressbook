package com.stevenhampton.addressbook.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * General helper class for logging tasks.
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoggingHelper implements InitializingBean {
    public static final String ANONYMOUS = "anon";

    @Value("${addressbook.logging.max-json-size:10000}")
    private int maxJsonSize;

    private ObjectMapper objectMapper;

    public static String toJson(ObjectMapper mapper, Object value, int maxOutputSize) {
        String result;
        value = Optional.ofNullable(value).orElse("null");
        try {
            result = mapper.writeValueAsString(value);
        } catch (Exception e) {
            result = "(unable to serialize for logging) " + e.getLocalizedMessage();
        }
        if (maxOutputSize <= 0 || StringUtils.length(result) <= maxOutputSize) {
            return result;
        } else {
            return String.format("%s ... (%d characters truncated to %d)", StringUtils.truncate(result, maxOutputSize),
                    StringUtils.length(result), maxOutputSize);
        }
    }

    public String toJson(Object value) {
        return toJson(objectMapper, value, maxJsonSize);
    }

    /**
     * @return a Jackson {@link ObjectMapper} configured specifically for logging purposes
     */
    public static ObjectMapper getLoggingObjectMapper() {
        var mapper = new ObjectMapper();
        var module = new SimpleModule();
        mapper.registerModule(module);
        mapper.registerModule(new Jdk8Module());
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
        return mapper;
    }

    public static String toString(Object value) {
        return toJson(getLoggingObjectMapper(), value, Integer.MAX_VALUE);
    }

    /**
     * Converts a {@link Throwable}'s stack trace into what is effectively a single line for CloudWatch and appends it
     * to the given log message - doing it this way rather than writing custom log converters, changing CloudWatch agent
     * settings etc. means that this always works everywhere, regardless of logging framework or destination.
     * @param e          {@link Throwable}
     * @param log        {@link Logger} to use
     * @param message    log message
     * @param parameters for message
     */
    public static void logThrowable(Throwable e, Logger log, String message, Object... parameters) {
        var messageTemplate = createMessageTemplateForThrowable(e, message);
        log.error(messageTemplate, parameters);
    }

    /**
     * Append stack trace contents from the given {@link Throwable} to the given {@link String} log message template.
     * @param e       {@link Throwable}
     * @param message log message
     * @return message template
     */
    public static String createMessageTemplateForThrowable(Throwable e, String message) {
        return message + ". Stack trace: " + toSingleLine(ExceptionUtils.getStackTrace(e));
    }

    /**
     * Turn a multi-line {@link String} into a single line for logging.
     * @param rawData to log
     * @return single line
     */
    public static String toSingleLine(String rawData) {
        return RegExUtils.replaceAll(rawData, "[\t\r\n]+", "\t");
    }

    public String getCurrentUsernameForLogging() {
        return ANONYMOUS;
    }

    public String getCurrentUsernameAsLoggingPrefix() {
        return initMessageBuffer(getCurrentUsernameForLogging()).toString();
    }

    /**
     * Logs all details for the given method call.
     * @param log       pass this in so finer-grained control is possible over what is logged or not
     * @param joinPoint join point at which to log
     * @return return value from method call
     * @throws Throwable pass any throwable up the call stack
     */
    public Object logMethodCall(Logger log, ProceedingJoinPoint joinPoint) throws Throwable {
        var methodName = joinPoint.getSignature().toString();
        String username = null;
        if (log.isTraceEnabled()) {
            username = getCurrentUsernameForLogging();
            var displayArguments = toJson(mapMethodParameters(joinPoint));
            var message = initMessageBuffer(username);
            message.append("calling ");
            message.append(methodName);
            if (!"{ }".equals(displayArguments)) {
                message.append(", arguments ");
                message.append(displayArguments);
            }
            log.trace(message.toString());
        }
        Object returnValue = joinPoint.proceed();
        if (log.isTraceEnabled()) {
            var displayReturnValue = toJson(returnValue);
            var message = initMessageBuffer(username);
            message.append("after ");
            message.append(methodName);
            message.append(" returning ");
            var returnType = ((MethodSignature)joinPoint.getSignature()).getReturnType().toGenericString();
            message.append(returnType);
            if (!StringUtils.equals("void", returnType) && !StringUtils.equals("{ }", displayReturnValue)) {
                message.append(" ");
                message.append(displayReturnValue);
            }
            log.trace(message.toString());
        }
        return returnValue;
    }

    public StringBuilder initMessageBuffer(String username) {
        var message = new StringBuilder();
        if (username != null) {
            message.append("[");
            message.append(username);
            message.append("] ");
        }
        return message;
    }

    public Map<String, Object> mapMethodParameters(ProceedingJoinPoint jp) {
        String[] argNames = ((MethodSignature)jp.getSignature()).getParameterNames();
        Object[] values = jp.getArgs();
        var params = new HashMap<String, Object>();
        if (argNames != null && argNames.length != 0) {
            for (int i = 0; i < argNames.length; i++) {
                params.put(argNames[i], values[i]);
            }
        }
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        // separate instance so it doesn't mess anything else up
        objectMapper = getLoggingObjectMapper();
    }
}

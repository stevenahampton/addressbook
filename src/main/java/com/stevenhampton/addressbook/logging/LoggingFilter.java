package com.stevenhampton.addressbook.logging;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Log all incoming requests and outgoing responses.
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LoggingFilter extends OncePerRequestFilter {
    private final @NonNull LoggingHelper loggingHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.debug("{}received request {} {}", loggingHelper.getCurrentUsernameAsLoggingPrefix(), request.getMethod(),
                request.getRequestURI());
        chain.doFilter(request, response);
        String contentType = response.getContentType();
        log.debug("{}sending response {}{}", loggingHelper.getCurrentUsernameAsLoggingPrefix(), response.getStatus(),
                Optional.ofNullable(contentType).map(ct -> String.format(" [%s]", ct)).orElse(""));
    }
}

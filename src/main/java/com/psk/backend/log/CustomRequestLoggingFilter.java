package com.psk.backend.log;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import javax.servlet.http.HttpServletRequest;


@Component
public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {

    public CustomRequestLoggingFilter() {
        super.setIncludeQueryString(true);
        super.setIncludePayload(true);
        super.setMaxPayloadLength(10000);
        super.setIncludeClientInfo(true);

    }
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (logger.isDebugEnabled()){
            logger.debug( message + request.getMethod() + " " + auth.getAuthorities());
        }
    }
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {}

}

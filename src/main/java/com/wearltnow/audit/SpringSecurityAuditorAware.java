package com.wearltnow.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import java.util.Optional;

@Component
@Slf4j
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra nếu authentication không null và đã được xác thực
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("Current user: {}", authentication.getName());
            return Optional.of(authentication.getName());
        }

      //  log.warn("No authenticated user found, returning empty Optional.");

        return Optional.empty();
    }
}

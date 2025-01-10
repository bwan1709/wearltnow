package com.wearltnow.job;

import com.wearltnow.repository.InvalidatedRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class TokenCleanupService {
    private final InvalidatedRepository invalidatedTokenRepository;

    public TokenCleanupService(InvalidatedRepository invalidatedTokenRepository) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        log.info("Running scheduled task to clean up expired tokens...");
        Date now = new Date();
        invalidatedTokenRepository.deleteByExpiryTimeBefore(now);
    }
}

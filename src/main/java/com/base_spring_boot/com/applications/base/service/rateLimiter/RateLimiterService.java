package com.base_spring_boot.com.applications.base.service.rateLimiter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class RateLimiterService {
    private final Bucket bucket;

    public RateLimiterService() {
        Bandwidth limit = Bandwidth.classic(75, Refill.greedy(10, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    public boolean tryConsumeRequest() {
        return bucket.tryConsume(1);
    }
}

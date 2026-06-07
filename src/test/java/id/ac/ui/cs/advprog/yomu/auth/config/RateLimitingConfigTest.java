package id.ac.ui.cs.advprog.yomu.auth.config;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitingConfigTest {

    @Test
    void loginRateLimitBucket_shouldCreateBucketWithDefaultLimits() {
        RateLimitingConfig config = new RateLimitingConfig();
        Bucket bucket = config.loginRateLimitBucket();

        assertNotNull(bucket);
        // Should allow consumption of at least 1 token initially
        assertTrue(bucket.tryConsume(1));
    }
}
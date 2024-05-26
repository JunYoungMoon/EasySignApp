package com.member.easysignapp.component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class APIRateLimiter {
    private final LettuceBasedProxyManager<String> proxyManager;
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public APIRateLimiter(RedisClient redisClient) {
        StatefulRedisConnection<String, byte[]> connection = redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        // redis
        // 만료 전략 포함은 설정하여도 period가 적용된다.
        this.proxyManager = LettuceBasedProxyManager.builderFor(connection)
                .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(10)))
                .build();
    }

    private Bucket getOrCreateBucket(String apiKey, long limit, long period) {
        return buckets.computeIfAbsent(apiKey, key -> {
            BucketConfiguration configuration = createBucketConfiguration(limit, period);
            return proxyManager.builder().build(key, configuration);
        });
    }

    private BucketConfiguration createBucketConfiguration(long limit, long period) {
        return BucketConfiguration.builder()
                .addLimit(Bandwidth.simple((int) limit, Duration.ofMillis(period)).withInitialTokens((int) limit))
                .build();
    }

    public boolean tryConsume(String apiKey, long limit, long period) {
        Bucket bucket = getOrCreateBucket(apiKey, limit, period);
        boolean consumed = bucket.tryConsume(1);
        log.info("API Key: {}, Consumed: {}, Time: {}", apiKey, consumed, LocalDateTime.now());
        return consumed;
    }
}
package com.member.easysignapp.aop;

import com.member.easysignapp.annotation.RateLimit;
import com.member.easysignapp.component.APIRateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private final APIRateLimiter apiRateLimiter;

    public RateLimitAspect(APIRateLimiter apiRateLimiter) {
        this.apiRateLimiter = apiRateLimiter;
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String apiKey = rateLimit.key();
        long limit = rateLimit.limit();
        long period = rateLimit.period();

        if (apiRateLimiter.tryConsume(apiKey, limit, period)) {
            return joinPoint.proceed();
        } else {
            throw new RuntimeException("Rate limit exceeded for key: " + apiKey);
        }
    }
}
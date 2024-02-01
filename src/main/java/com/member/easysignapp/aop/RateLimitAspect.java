package com.member.easysignapp.aop;

import com.member.easysignapp.annotation.RateLimit;
import com.member.easysignapp.component.APIRateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RateLimitAspect {

    private final APIRateLimiter apiRateLimiter;

    private final MessageSourceAccessor messageSourceAccessor;

    public RateLimitAspect(APIRateLimiter apiRateLimiter, MessageSourceAccessor messageSourceAccessor) {
        this.apiRateLimiter = apiRateLimiter;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String apiKey = rateLimit.key();
        long limit = rateLimit.limit();
        long period = rateLimit.period();

        if (apiRateLimiter.tryConsume(apiKey, limit, period)) {
            return joinPoint.proceed();
        } else {
            String successMessage = messageSourceAccessor.getMessage("rateLimit.msessage", new Object[]{apiKey});

            throw new RuntimeException(successMessage);
        }
    }
}
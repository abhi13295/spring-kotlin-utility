package com.outleap.demo.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

@Aspect
@Component
@Slf4j
public class LockAspectDefinition  {

    private final LockRegistry lockRegistry;

    public LockAspectDefinition(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
        // This is empty as per requirement
    }

    @Around("publicMethod() && @within(executeWithLock)")
    Object executeWithLock(ProceedingJoinPoint pjp, ExecuteWithLock executeWithLock) throws Throwable {
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        Method method = signature.getMethod();

        String lockKey = executeWithLock.key();

        Object[] args = pjp.getArgs();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation paramAnnotation : parameterAnnotations[argIndex]) {
                if (paramAnnotation instanceof LockKey) {
                    lockKey = (String) args[argIndex];
                    break;
                }
            }
        }

        if (StringUtils.isEmpty(lockKey)) {
            throw new RuntimeException("Lock key is not defined");
        }

        final String lockKeyFinal = lockKey;

        Lock obtain = lockRegistry.obtain(lockKeyFinal);

        log.info("taking lock, " + Thread.currentThread().getName());
        if (executeWithLock.executeOnce()) {
            if (!obtain.tryLock()) {
                return null;
            }
        } else {
            obtain.lock();
        }

        try {
            return pjp.proceed();
        } finally {
            log.info("releasing lock, " + Thread.currentThread().getName());
            obtain.unlock();
        }
    }

}

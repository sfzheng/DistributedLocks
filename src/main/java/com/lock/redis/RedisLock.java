/**
 * FileName: RedisLock
 * Author:   songfz
 * Date:     2020/5/28 16:18
 * Description: redis锁
 */
package com.lock.redis;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br>
 * 〈redis锁〉
 *
 * @author songfz
 * @create 2020/5/28
 * @since 1.0.0
 */
@Component
@Aspect
@Slf4j
public class RedisLock {

    private RedissonClient redissonClient;

    public RedisLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Pointcut(value = "@annotation(com.lock.redis.RedisLockAop)")
    public void pointCut() {

    }

    @Before(value = "pointCut()")
    public void before() {

    }

    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        RLock lock;

        //1.获取到所有的参数值的数组
            Object[] args = joinPoint.getArgs();
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            //2.获取到方法的所有参数名称的字符串数组
            String[] parameterNames = methodSignature.getParameterNames();

            Method method = methodSignature.getMethod();

            RedisLockAop redis = method.getAnnotation(RedisLockAop.class);
            lock = redissonClient.getLock(redis.lockKey());
            try {  if (lock.tryLock(redis.waitTime(), redis.leaseTime(), redis.timeUnit())) {
                StopWatch stopWatch = new StopWatch(String.valueOf(args[0]));
                stopWatch.start();
                try {
                   return joinPoint.proceed(args);
               }catch (Throwable throwable) {
                   throwable.printStackTrace();
               }finally {
                    stopWatch.stop();
                   log.info("一次用时：【{}】:[【{}】",stopWatch.getId(),stopWatch.getLastTaskTimeNanos());
                }
            }
            return -1;
        } catch (Exception e) {
           log.error(e.getMessage(),e);
        } finally {
            if (lock != null && lock.isHeldByThread(Thread.currentThread().getId())) {
                lock.unlock();
            }
        }
        return -1;
    }
}

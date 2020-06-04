package com.lock.redis;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author songfz
 * @date 2020/6/2 13:14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisLockAop {

    /**
     * 锁名称
     * @return
     */
    String lockKey() default "default";

    /**
     * 等待时间
     * @return
     */
    long waitTime() default 1;

    /**
     * 锁持有时间
     * @return
     */
    long leaseTime() default 1;

    /**
     * 时间单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 对当前锁描述
     * @return
     */
    String description() default "";
}

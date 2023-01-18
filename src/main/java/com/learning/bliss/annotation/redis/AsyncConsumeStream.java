package com.learning.bliss.annotation.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/6 12:33
 * @Version 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RUNTIME)
public @interface AsyncConsumeStream {
    String streamKey() default "";

    String consumerGroup() default "";

    String consumerName() default "";
}

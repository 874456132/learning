package com.learning.bliss.annotation.redis;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 延迟队列
 *
 * @Author xuexc
 * @Date 2023/1/6 12:33
 * @Version 1.0
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface AsyncConsumeZset {

    String queue() default "queue";

}


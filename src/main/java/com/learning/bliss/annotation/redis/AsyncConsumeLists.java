package com.learning.bliss.annotation.redis;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/6 12:34
 * @Version 1.0
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface AsyncConsumeLists {

    String queue() default "queue";

    long timeout() default 30;

}

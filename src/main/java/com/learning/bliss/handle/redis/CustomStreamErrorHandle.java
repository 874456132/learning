package com.learning.bliss.handle.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

/**
 * Stream
 *
 * @Author xuexc
 * @Date 2023/1/6 12:28
 * @Version 1.0
 */
@Slf4j
public class CustomStreamErrorHandle implements Thread.UncaughtExceptionHandler, ErrorHandler {

    /**
     * 当执行stream队列的线程池为ForkJoinPool时，才实现Thread.UncaughtExceptionHandler接口
     * 未捕获的线程异常
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }

    @Override
    public void handleError(Throwable throwable) {
        log.error("redis error:", throwable);
    }
}
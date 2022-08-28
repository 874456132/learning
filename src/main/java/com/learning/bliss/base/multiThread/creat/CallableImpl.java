package com.learning.bliss.base.multiThread.creat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * java 多线程
 * 创建一个线程，第三种方法是实现Callable接口。
 *
 * @Author: xuexc
 * @Date: 2021/1/3 16:23
 * @Version 0.1
 */
public class CallableImpl implements Callable<Integer> {
    private String  threadName = "";

    public CallableImpl() {
    }
    public CallableImpl(String name) {
        threadName = name;
    }

    @Override
    public Integer call() throws Exception {
        int i = 0;
        for (; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + "-->" + i);
            Thread.sleep(2000);
        }
        return i;
    }

    public Integer startCall() throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new CallableImpl());
        new Thread(futureTask, this.threadName).start();
        return futureTask.get();
    }
}

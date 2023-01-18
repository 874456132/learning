package com.learning.bliss.demo.base.multiThread.creat;

import java.util.concurrent.ExecutionException;

/**
 * @Author: xuexc
 * @Date: 2021/1/3 17:04
 * @Version 0.1
 */
public class ThreadCreatMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*1. 调用线程的start()方法来启动线程，真正实现了多线程运行（同步）。启动一个线程实际是请求Java虚拟机运行相应的线程，而这个线程何时能够运行是由线程调度器决定的。start()调用结束只是表明程序已经将运行线程的申请提交给了虚拟机。
        2. 不显式调用run()方法，run()称为线程体，此方法相当于线程的任务处理逻辑的入口方法。它包含了要执行的这个线程的内容，它由Java虚拟机在运行相应线程时自动调用。当应用程序显示调用此方法时，线程之间顺序执行，没有达到多线程同步执行的目的*/
        RunnableImpl implRunnable = new RunnableImpl("thread-impl-1");
        for (int i = 0; i < 5; i++) {
            implRunnable.start();
        }
        /*ThreadImpl threadImpl = new ThreadImpl("thread-extend-1");
        for(int i=0;i<5;i++){
            threadImpl.start();
            //threadImpl.run();
        }*/
        for (int i = 0; i < 5; i++) {
            CallableImpl callableImpl = new CallableImpl("thread-impl-1");
            Integer result = callableImpl.startCall();
            System.out.println(Thread.currentThread().getName() + "----" + result);
        }

    }
}


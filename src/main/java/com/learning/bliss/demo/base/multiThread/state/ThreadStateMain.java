package com.learning.bliss.demo.base.multiThread.state;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @Author: xuexc
 * @Date: 2021/1/3 17:04
 * @Version 0.1
 */
public class ThreadStateMain {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=============第 1 种切换状态：新建→运行→终止=============");
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程：" + Thread.currentThread().getName() + "调用run()，状态：" + Thread.currentThread().getState().toString());
                //System.out.println("线程：" + Thread.currentThread().getName() +"，执行成功");
            }
        }, "Thread1");
        System.out.println("调用start()之前，线程：" + thread1.getName() + "，状态：" + thread1.getState().toString());
        thread1.start();
        Thread.sleep(1000L);
        System.out.println("调用start()之后，线程：" + thread1.getName() + "，状态：" + thread1.getState().toString());
        System.out.println("\n\n");

        System.out.println("=============第 2 种切换状态：新建→运行→等待→运行→终止=============");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                for (; now.until(LocalDateTime.now(), ChronoUnit.MILLIS) < 1000;) ;//运行两秒，为的是打印出RUNNABLE状态
                System.out.println("线程：" + Thread.currentThread().getName() + "调用run()，状态：" + Thread.currentThread().getState().toString());
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; now.until(LocalDateTime.now(), ChronoUnit.MILLIS) < 5000; i++) ;
            }
        }, "Thread2");
        System.out.println("调用start()之前，线程：" + thread2.getName() + "，状态：" + thread2.getState().toString());
        thread2.start();
        System.out.println("调用start()之后，线程：" + thread2.getName() + "，状态：" + thread2.getState().toString());
        Thread.sleep(2000);
        System.out.println("调用sleep()之后，线程：" + thread2.getName() + "，状态：" + thread2.getState().toString());
        Thread.sleep(1500);
        System.out.println("调用sleep()之后被唤醒，线程：" + thread2.getName() + "，状态：" + thread2.getState().toString());
        Thread.sleep(3000);
        System.out.println("线程：" + thread2.getName() + "执行结束，状态：" + thread2.getState().toString());
        System.out.println("\n\n");

        System.out.println("=============第 3 种切换状态：新建→运行→阻塞→运行→终止=============");
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                System.out.println("线程：" + Thread.currentThread().getName() + "执行run()，状态：" + Thread.currentThread().getState().toString());
                for (int i = 0; now.until(LocalDateTime.now(), ChronoUnit.MILLIS) < 2000; i++) ;
                synchronized (ThreadStateMain.class) {
                    System.out.println("线程：" + Thread.currentThread().getName() + "子线程拿到锁时，状态：" + Thread.currentThread().getState().toString());
                    for (int i = 0; now.until(LocalDateTime.now(), ChronoUnit.MILLIS) < 4000; i++) ;
                }
                for (int i = 0; now.until(LocalDateTime.now(), ChronoUnit.MILLIS) < 6000; i++) ;
            }
        }, "Thread3");
        synchronized (ThreadStateMain.class) {
            System.out.println("调用start()之前，线程：" + thread3.getName() + "状态：" + thread3.getState().toString());
            thread3.start();
            System.out.println("调用start()之后，线程：" + thread3.getName() + "状态：" + thread3.getState().toString());
            Thread.sleep(2500);
            System.out.println("线程：" + Thread.currentThread().getName() + "主线程拿到锁时，状态：" + thread3.getState().toString());
        }
        thread3.interrupt();//终端线程
        System.out.println("线程：" + thread3.getName() + "执行中断interrupt()方法后，状态：" + thread3.getState().toString());//中断方法后线程池状态仍为BLOCKED，说明此析构函数并不是立即终端线程的
        Thread.sleep(3500);
        System.out.println("线程：" + thread3.getName() + "执行结束，状态：" + thread3.getState().toString());
    }
}


package com.learning.bliss.demo.base.multiThread.creat;

/**
 * java 多线程
 * 创建一个线程，最简单的方法是创建一个实现 Runnable 接口的类。本质上是实现了run()方法
 * 
 * @Author: xuexc
 * @Date: 2021/1/3 16:23
 * @Version 0.1
 */
public class RunnableImpl implements Runnable {

    private Thread t;
    private String threadName;

    public RunnableImpl(String name) {
        threadName = name;
        System.out.println("Creating " + threadName);
    }

    @Override
    public void run() {
        System.out.println("Running " + threadName);
        try {
            for (int i = 4; i > 0; i--) {
                System.out.println("Thread: " + threadName + ", " + i);
                // 让线程睡眠一会
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " + threadName + " interrupted.");
        }
        System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() {
        //System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}


package com.learning.bliss.base.multiThread.creat;

/**
 * java 多线程
 * 创建一个线程的第二种方法是继承 Thread 类，本质上是重写run()方法。
 * 该方法尽管被列为一种多线程实现方式，实际上也是实现了 Runnable 接口的一个实例（Thread实现的是Runnable接口）。
 * 具体实现步骤：
 * 继承 Thread 类
 * 创建一个该类的实例。
 * 重写run()方法，该方法是新线程的入口点
 * 调用 start() 方法执行线程。
 *
 * @Author: xuexc
 * @Date: 2021/1/3 16:23
 * @Version 0.1
 */
public class ThreadImpl extends Thread {

    /*具体实现步骤：
    继承 Thread 类
    创建一个该类的实例。
    重写run()方法，该方法是新线程的入口点
    调用 start() 方法执行线程。*/

    private Thread t;
    private String threadName;

    public ThreadImpl(String name) {
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
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " + threadName + " interrupted.");
        }
        System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

}

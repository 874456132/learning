package com.learning.bliss.demo.base.lock;

/**
 * synchronized作用范围
 *
 * @Author xuexc
 * @Date 2021/9/22 19:55
 * @Version 1.0
 */
public class SynchronizedDemo {
    /*************** synchronized的作用范围 ***************/
    /****** synchronized其实是通过对象监视器实现的，如果对象非同一个对象（对象地址指针不同），则就不会触发监视器一系列线程阻塞操作 ******/
    //1、静态方法上，只要线程间调用的是同一个静态方法（不同线程对应同一个地址的方法），则就是线程安全的
    /*@SneakyThrows
    private synchronized static void execute(){
        System.out.println(Thread.currentThread().getName() + "开始执行");
        Thread.sleep(3000L);
        System.out.println(Thread.currentThread().getName() + "执行结束");
    }
    public static void main(String[] args) {
        //scene 1 线程安全
        *//*new Thread(() -> {
            new SynchronizedDemo().execute();
        }, "thread - 1").start();

        new Thread(() -> {
            new SynchronizedDemo().execute();
        }, "thread - 2").start();*//*
        //scene 2  线程安全
        new Thread(() -> {
            execute();
        }, "thread - 1").start();

        new Thread(() -> {
            execute();
        }, "thread - 2").start();
    }*/
    //2、普通方法上，只要线程间调用的是同一个方法（不同线程对应不同方法，不同线程间普通方法在线程创建时拷贝到线程对象中），则就是线程安全的
    /*@SneakyThrows
    private synchronized void execute(){
        System.out.println(Thread.currentThread().getName() + "开始执行");
        Thread.sleep(3000L);
        System.out.println(Thread.currentThread().getName() + "执行结束");
    }

    public static void main(String[] args) {
        //scene 1 非线程安全
        *//*new Thread(() -> {
            new SynchronizedDemo().execute();
        }, "thread - 1").start();

        new Thread(() -> {
            new SynchronizedDemo().execute();
        }, "thread - 2").start();*//*
        //scene 2 线程安全
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
        new Thread(() -> {
            synchronizedDemo.execute();
        }, "thread - 1").start();

        new Thread(() -> {
            synchronizedDemo.execute();
        }, "thread - 2").start();
    }*/

    //3、静态变量上，只要线程间使用的是同一个静态变量，则就是线程安全的
    private static Object arg = "";

    private void execute() {
        synchronized (arg) {
            System.out.println(Thread.currentThread().getName() + "开始执行");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "执行结束");
        }
    }

    public static void main(String[] args) {
        //scene 1 线程安全
        new Thread(() -> {
            new SynchronizedDemo().execute();
        }, "thread - 1").start();
        new Thread(() -> {
            new SynchronizedDemo().execute();
        }, "thread - 2").start();
        //scene 2 非线程安全
        new Thread(() -> {
            new SynchronizedDemo().execute();
        }, "thread - 1").start();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            arg = new Object();//arg 静态变量对象发生改变，地址指针改变
            new SynchronizedDemo().execute();
        }, "thread - 2").start();
    }
}

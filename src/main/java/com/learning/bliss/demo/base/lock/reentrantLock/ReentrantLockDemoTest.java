package com.learning.bliss.demo.base.lock.reentrantLock;

import lombok.SneakyThrows;

import java.util.concurrent.locks.Lock;

/**
 * 手写可重入锁测试类
 *
 * @Author xuexc
 * @Date 2021/9/22 19:55
 * @Version 1.0
 */
public class ReentrantLockDemoTest {

    private static Lock lock = new ReentrantLockDemo();//可以直接使用ReentrantLock替换ReentrantLockDemo

    /*************** 可重入锁：ReentrantLock ***************/
    //ReentrantLock(boolean fair) fair是否公平锁 无fair时，默认非公平锁，每次重入时，锁状态state加1
    /*@SneakyThrows
    private static void add(int i){
        lock.lock();
        ++i;
        try {
            Thread.sleep(1000);
            System.out.println("线程：" + Thread.currentThread().getName() + "第" + ((ReentrantLockDemo)lock).getHoldCount() + "次获取锁");
            if(i < 5) {
                add(i);
            }
        } finally {
            lock.unlock();//当锁还被占用时，则锁释放失败。
            System.out.println("线程：" + Thread.currentThread().getName() + "，第" + (5 - i + 1) + "次执行解锁后，当前同一把锁加锁的次数：" + ((ReentrantLockDemo)lock).getHoldCount() + "当前锁状态：" + ((ReentrantLockDemo)lock).isLocked());

        }
    }

    public static void main(String[] args) {
        add(0);
        new Thread(() -> {
            add(0);
        }, "Thread-1").start();
        System.out.println();
    }*/
    /*************** lock()与lockInterruptibly() ***************/
    /*@SneakyThrows
    private static void add(){
        *//*lock 与 lockInterruptibly比较区别在于：
        lock 优先考虑获取锁，待获取锁成功后，才响应中断。
        lockInterruptibly 优先考虑响应中断，而不是响应锁的普通获取或重入获取。
        ReentrantLock.lockInterruptibly允许在等待时由其它线程调用等待线程的Thread.interrupt方法来中断等待线程的等待而直接返回，这时不用获取锁，而会抛出一个InterruptedException。 ReentrantLock.lock方法不允许Thread.interrupt中断,即使检测到Thread.isInterrupted,一样会继续尝试获取锁，失败则继续休眠。只是在最后获取锁成功后再把当前线程置为interrupted状态,然后再中断线程。*//*
        //lock.lock();//通过lock()方法获取锁的线程在线程中断时，会抛出InterruptedException异常
        lock.lockInterruptibly();
        try {
            System.out.println("线程：" + Thread.currentThread().getName() + "获取锁的次数：" + ((ReentrantLockDemo)lock).getHoldCount());
            Thread.sleep(6000);
        } finally {
            lock.unlock();//当锁还被占用时，则锁释放失败。
        }
    }
    @SneakyThrows
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            add();
        }, "Thread-1");
        Thread thread2 = new Thread(() -> {
            add();
        }, "Thread-2");

        thread1.start();
        Thread.sleep(1000);
        thread2.start();
        Thread.sleep(1000);
        thread2.interrupt();
    }*/

    /************** tryLock()、tryLock(long time, TimeUnit unit) *************/
    /*@SneakyThrows
    private static void add(){

        //if(lock.tryLock()){
        if(lock.tryLock(500, TimeUnit.MILLISECONDS)){
            try {
                System.out.println("线程：" + Thread.currentThread().getName() + "获取到锁了");
                Thread.sleep(2000);
            } finally {
                lock.unlock();//当锁还被占用时，则锁释放失败。
            }
        } else {
            System.out.println("线程：" + Thread.currentThread().getName() + "未获取到锁");
        }

    }
    @SneakyThrows
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            add();
        }, "Thread-1");
        Thread thread2 = new Thread(() -> {
            add();
        }, "Thread-2");

        thread1.start();
        Thread.sleep(1000);
        thread2.start();
    }*/
    //验证setState线程安全问题
    @SneakyThrows
    private static void add(int i) {
        lock.lock();
        ++i;
        try {
            if(i < 50) {
                add(i);
            } else {
                System.out.println("当前state:" + ((ReentrantLockDemo)lock).getHoldCount());
            }
        } finally {
            lock.unlock();//当锁还被占用时，则锁释放失败。
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            add(0);
        }, "Thread-1");
        thread1.start();
    }
}

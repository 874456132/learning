package com.learning.bliss.base.lock;

import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * 独占锁简单原理模拟
 *
 * @Author xuexc
 * @Date 2021/10/28 20:19
 * @Version 1.0
 */
public class MyLock implements Lock {

    volatile AtomicReference<Thread> owner = new AtomicReference<>();
    volatile LinkedBlockingQueue<Thread> queue = new LinkedBlockingQueue();
    @Override
    public void lock() {
        boolean addQ = true;
        while(!tryLock()) {//使用while 防止伪唤醒（非unpark唤醒）
            if(addQ){
                //没拿到锁，加入等待集合
                queue.offer(Thread.currentThread());
                addQ = false;

            }
            LockSupport.park(Thread.currentThread());
            //进入阻塞
        }
        queue.remove(Thread.currentThread());
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        //CAS将当前线程赋值给拥有者
        return owner.compareAndSet(null, Thread.currentThread());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        //释放owner
        if(owner.compareAndSet(Thread.currentThread(), null)){
            //释放成功，通知等待者
            Iterator<Thread> iterator = queue.iterator();
            while (iterator.hasNext()){
                Thread next = iterator.next();
                LockSupport.unpark(next);
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    static int a = 0;
    static int b = 0;
    static MyLock lock = new MyLock();
    private static void add(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*a++;
        b++;*/
        lock.lock();
        try{
            a++;b++;
        } finally {
            lock.unlock();
        }
    }
    @SneakyThrows
    public static void main(String[] args) {
        /*for(int i = 0; i < 1000; i++){
            new Thread(() -> {
                add();
            }).start();
        }
        Thread.sleep(5000);
        System.out.println(a);
        System.out.println(b);*/
    }
}

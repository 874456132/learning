package com.learning.bliss.base.lock.reentrantReadWriteLock;

import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReentrantReadWriteLock的使用
 *
 * @Author xuexc
 * @Date 2021/10/25 20:50
 * @Version 1.0
 */
public class ReentrantReadWriteLockDemoTest {
    static final Queue queue = new Queue();
    public static void main(String[] args) {

        //一共启动6个线程，3个读线程，3个写线程
        for (int i = 0; i < 3; i++) {
            //启动1个读线程
            new Thread() {
                public void run() {
                    while (true) {
                        queue.get();
                    }
                }

            }.start();
            //启动1个写线程
            new Thread() {
                public void run() {
                    while (true) {
                        queue.put(new Random().nextInt(10000));
                    }
                }
            }.start();
        }
    }
    static class Queue {
        //共享数据，只能有一个线程能写该数据，但可以有多个线程同时读该数据。
        private static int data;

        private static ReentrantReadWriteLockDemo lock = new ReentrantReadWriteLockDemo(true);

        // 读数据
        public void get() {
            // 加读锁
            lock.readLock().lock();
            try {
                System.out.println(Thread.currentThread().getName() + " be ready to read data!");
                Thread.sleep((long) (Math.random() * 1000));
                System.out.println(Thread.currentThread().getName() + " have read data :" + this.data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放读锁
                lock.readLock().unlock();
            }
        }

        // 写数据
        public void put(int data) {
            // 加写锁
            lock.writeLock().lock();
            try {
                System.out.println(Thread.currentThread().getName() + " be ready to write data!");
                Thread.sleep((long) (Math.random() * 1000));
                this.data = data;
                System.out.println(Thread.currentThread().getName() + " have write data: " + this.data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放写锁
                lock.writeLock().unlock();
            }

        }
    }
}

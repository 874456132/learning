package com.learning.bliss.base.queue;

import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;

/**
 * @Author: xuexc
 * @Date: 2022/4/17 21:1
 * @Version 0.1
 */
public class SynchronousQueueDemo {
    private SynchronousQueue<String> syncQueue = new SynchronousQueue(true);
    private static final CountDownLatch begin = new CountDownLatch(1);
    private static CountDownLatch end;


    private void product(int prdCount) {
        for (int i = 0; i < prdCount; i++) {
            String finalI = i + "";
            new Thread(() -> {
                try {
                    //begin.await();
                    syncQueue.put(finalI);
                    System.out.println("生产了第" + finalI + "双鞋子");
                } catch (InterruptedException e) {
                    System.out.println("生产线程 " + Thread.currentThread().getName() + " 被意外终止了");

                } finally {
                    end.countDown();
                }
            }, "线程" + (i + 1) + "").start();
        }
    }

    private void consum(int cosCount) {
        for (int i = 0; i < cosCount; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread(() -> {
                try {
                    //begin.await();
                    String consum = syncQueue.take();
                    Thread.sleep(20);
                    System.out.println("卖出了第" + consum + "双鞋子");
                } catch (InterruptedException e) {
                    System.out.println("消费线程 " + Thread.currentThread().getName() + " 被意外终止了");
                } finally {
                    end.countDown();
                }
            }, "线程" + (i + 1) + "").start();
        }
    }

    public static void main(String[] args) {
        SynchronousQueueDemo demo = new SynchronousQueueDemo();
        end = new CountDownLatch(20);
        System.out.println("Game Start");
        demo.product(10);
        demo.consum(10);
        //begin.countDown();
        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Game End");
    }
}

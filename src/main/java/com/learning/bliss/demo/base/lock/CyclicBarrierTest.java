package com.learning.bliss.demo.base.lock;

import java.util.concurrent.*;

/**
 * 循环屏障（线程栅栏）
 *
 * @Author xuexc
 * @Date 2021/12/14 15:24
 * @Version 1.0
 */
public class CyclicBarrierTest {
    private final static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
    private final static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(12);

    public static void main(String[] args) {
        new CyclicBarrierTest().threadCyclicRun();
        //new CyclicBarrierTest().awaitForReset();
    }

    private void threadCyclicRun() {
        for (int i = 0; i < 9; i++) {
            executor.submit(new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "创建工作线程");
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName() + "开始执行");
                    // 工作线程开始处理，这里用Thread.sleep()来模拟业务处理
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + "执行完毕");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
    }

    /**
     * 执行await挂起的线程被reset时，抛异常
     */
    private void awaitForReset() {
        executor.submit(new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "创建工作线程");
                cyclicBarrier.await();
                System.out.println(Thread.currentThread().getName() + "开始执行");
                // 工作线程开始处理，这里用Thread.sleep()来模拟业务处理
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + "执行完毕");
            } catch (InterruptedException e) {
                System.out.println("throw InterruptedException异常");
            } catch (BrokenBarrierException e) {
                System.out.println("throw BrokenBarrierException异常");
            }
        }));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cyclicBarrier.reset();
    }

}

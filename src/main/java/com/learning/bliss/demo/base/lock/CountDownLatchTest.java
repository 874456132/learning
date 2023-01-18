package com.learning.bliss.demo.base.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
  * 倒计数器
  * @Author: xuexc
  * @Date: 2021/12/13 21:21
  * @Version 0.1
  */
public class CountDownLatchTest {
    private static CountDownLatch cdl;
    private static ThreadPoolExecutor executor;

    public static void main(String[] args) {
        new CountDownLatchTest().runSports(10);
        /*new CountDownLatchTest().baseTWaitChildT(5);
        cdl.await();
        executor.shutdownNow();
        System.out.println("子线程全部执行完毕");*/
        //new CountDownLatchTest().runSportSigle(10);
    }

    /**
     * 主线程等待子线程执行完毕后再继续执行
     * @param num
     */
    private void baseTWaitChildT(int num){
        cdl = new CountDownLatch(num);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(num);
        for (int i = 0; i < num; i++) {
            int finalI = i;
            executor.submit(new Thread( () -> {
                try {
                    Thread.sleep(100);
                    System.out.println("子线程 " + (finalI + 1) + "开始执行");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("子线程 " + (finalI + 1) + "执行结束");
                    cdl.countDown();
                }
            }));
        }
    }

    /**
     * 跑步比赛（num人一块比赛）
     * 模拟100米赛跑，num名选手准备就绪，待裁判枪响，所有人冲刺，到达终点，比赛结束
     * @param num
     */
    private void runSports(int num){
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(num);

        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(num);
        for (int i = 0; i < num; i++) {
            int finalI = i;
            executor.submit(new Thread( () -> {
                try {
                    Thread.sleep(100);
                    System.out.println((finalI + 1) + " 号选手准备就绪");
                    begin.await();
                    System.out.println((finalI + 1) + " 号选手加油中");
                    Thread.sleep(1000);
                    System.out.println((finalI + 1) + " 号选手到达终点");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    end.countDown();
                }
            }));
        }
        System.out.println("Game Start");
        try {
            Thread.sleep(1000);
            begin.countDown();
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Game End");
        executor.shutdownNow();
    }

    private void runSportSigle(int num){
        final CountDownLatch countDownLatch = new CountDownLatch(num);

        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(num);
        for (int i = 0; i < num; i++) {
            int finalI = i;
            executor.submit(new Thread(() -> {
                try {
                    System.out.println((finalI + 1) + " 号选手准备就绪");
                    countDownLatch.countDown();
                    Thread.sleep(1000);
                    countDownLatch.await();
                    System.out.println((finalI + 1) + " 号选手到达终点");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {

                }
            }));
        }
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
    }
}

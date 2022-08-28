package com.learning.bliss.base.lock;

import java.util.concurrent.Semaphore;

/**
  * 信号量使用测试类
  * @Author: xuexc
  * @Date: 2021/12/12 17:07
  * @Version 0.1
  */
public class SemaphoreTest {
    private static Semaphore semaphore = new Semaphore(5);

    public static void main(String[] args) {
        for(int i = 0; i < 10; i++){
            new Thread(()->{
                new SemaphoreTest().carPark();
            }, (i + 1) + "").start();
        }
    }

    //入库
    public void carPark(){
        try {
            semaphore.acquire();
            System.out.println("车编号 " + Thread.currentThread().getName() + " 入库");
            Thread.sleep(2000);
            System.out.println("车编号 " + Thread.currentThread().getName() + " 出库");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

}

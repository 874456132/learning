package com.learning.bliss.demo.base.multiThread.terminated;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 线程终止
 *
 * @Author: xuexc
 * @Date: 2021/1/3 17:04
 * @Version 0.1
 */
public class ThreadTerminatedMain {
    private static int i = 0, j = 0;
    //volatile修饰符用来保证其它线程读取的总是该变量的最新的值
    public static volatile boolean flag = true;

    public static void main(String[] args) throws InterruptedException {
        /*System.out.println("=============第 1 种 stop()终止线程，破坏线程安全（强行将程序中断，无法保证线程的原子性），JDK不建议使用=============");
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程：" + Thread.currentThread().getName() + "调用run()，状态：" + Thread.currentThread().getState().toString());
                synchronized (this) {
                    ++i;
                    try {
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ++j;
                }
            }
        }, "Thread1");
        System.out.println("调用start()之前，i=" + i + "，j=" + j);
        thread1.start();
        System.out.println("调用start()之后，i=" + i + "，j=" + j);
        Thread.sleep(1000);
        thread1.stop();
        System.out.println("调用stop()之后，i=" + i + "，j=" + j);*/

        /*System.out.println("=============第 2 种 标志位终止线程=============");
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程：" + Thread.currentThread().getName() + "调用run()，状态：" + Thread.currentThread().getState().toString());
                while (flag) {
                    ++i;
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ++j;
                }
            }
        }, "Thread3");
        System.out.println("调用start()之前，i=" + i + "，j=" + j);
        thread3.start();
        System.out.println("调用start()之后，i=" + i + "，j=" + j);
        Thread.sleep(2000);
        flag = false;
        System.out.println("标志位修改之后，i=" + i + "，j=" + j);
        thread3.join();
        System.out.println("子线程执行完成后，i=" + i + "，j=" + j);*/

        System.out.println("=============第 3 种 Interrupt()终止线程，线程安全=============");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程：" + Thread.currentThread().getName() + "调用run()，状态：" + Thread.currentThread().getState().toString());
                /*synchronized (this) {
                    ++i;
                    try {
                        //Thread.currentThread().wait(5000);
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) { //当抛出InterruptedException中断异常时，Java虚拟机会先将该线程的中断标识位清除，然后抛出InterruptedException，此时调用isInterrupted()方法将会返回false
                        e.printStackTrace();
                    }
                    ++j;
                }
                //调用 interrupt() 方法仅仅是在当前线程中打一个停止的标记，并不是真的停止线程。  Thread.interrupted()会清除中断标记位
                for(; i < 100000; ){
                    i++;
                }*/
                for(; i < 100000; ){
                    if(Thread.currentThread().isInterrupted()){
                       break;
                    }
                    i++;
                }
            }
        }, "Thread2");
        System.out.println("调用start()之前，i=" + i + "，j=" + j);
        thread2.start();
        Thread.sleep(5);
        System.out.println("调用start()之后，i=" + i + "，j=" + j);
        thread2.interrupt();
        System.out.println("线程：" + thread2.getName()  + "，状态：" + thread2.getState() + "，中断状态：" + thread2.isInterrupted());
        thread2.join();
        System.out.println("线程：" + thread2.getName()  + "执行结束，状态：" + thread2.getState() + "，中断状态：" + thread2.isInterrupted());
        System.out.println("调用interrupt()之后，i=" + i + "，j=" + j);
    }
}


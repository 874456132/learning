package com.learning.bliss.demo.base.multiThread.threadPool;


import com.learning.bliss.config.threadPool.ThreadPoolExecutorFactory;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * 线程池
 *
 * @Author xuexc
 * @Date 2021/6/28 17:43
 * @Version 1.0
 */
public class ThreadPoolDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("线程池开始初始化时间：" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
        ThreadPoolExecutor executor = ThreadPoolExecutorFactory.threadPoolExecutor2;
        //ScheduledExecutorService executor = ThreadPoolExecutorFactory.threadPoolExecutor6_1;
        System.out.println("线程池初始化结束时间：" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));

        /*System.out.println("【初始化线程池】核心线程数：" + executor.getCorePoolSize());
        System.out.println("【初始化线程池】最大线程数：" + executor.getMaximumPoolSize());
        System.out.println("【初始化线程池】空闲线程（非核心线程）等待时间：" + executor.getKeepAliveTime(TimeUnit.SECONDS));*/
        System.out.println("\n\n");
        for (int i = 0; i < 10; i++) {
            //RunnableImpl t = new RunnableImpl("thread-task-" + i);
            RunnableImpl t = new RunnableImpl("");
            System.out.println("当前线程" + t.getThreadName() + "->" + t.toString());
            System.out.println("线程池开始执行时间：" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
            executor.submit(t);
            //executor.schedule(t, 5, TimeUnit.SECONDS);//5秒后执行
            //executor.scheduleAtFixedRate(t, 5, 2,TimeUnit.SECONDS);//5秒后执行任务，每隔2秒遍历一次，如果上一次任务执行时间超过间隔时间，那么下次任务立即执行
            //executor.scheduleWithFixedDelay(t, 5, 2,TimeUnit.SECONDS);//5秒后执行任务，每隔2秒遍历一次，如果上一次任务执行时间超过间隔时间，那么下次任务继续等待间隔时间后再执行
        }
        System.out.println("尝试关闭线程池：" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
        //executor.shutdown();//优雅关闭线程池，已经提交的线程继续执行，未提交的线程终止
        //executor.shutdownNow();//立即关闭线程池
        /*System.out.println("【线程池】核心线程数：" + executor.getCorePoolSize());
        System.out.println("【线程池】当前活动线程数：" + executor.getActiveCount());
        System.out.println("【线程池】当前总线程数：" + executor.getPoolSize());
        System.out.println("【线程池】队列中等待执行的任务数：" + executor.getQueue().size());
        System.out.println("【线程池】曾经创建过的最大线程数：" + executor.getLargestPoolSize());
        System.out.println("【线程池】已执行完任务数：" + executor.getCompletedTaskCount());
        System.out.println("\n\n");

        Thread.sleep(5000L);
        System.out.println("【线程池】核心线程数：" + executor.getCorePoolSize());
        System.out.println("【线程池】当前活动线程数：" + executor.getActiveCount());
        System.out.println("【线程池】当前总线程数：" + executor.getPoolSize());
        System.out.println("【线程池】队列中等待执行的任务数：" + executor.getQueue().size());
        System.out.println("【线程池】曾经创建过的最大线程数：" + executor.getLargestPoolSize());
        System.out.println("【线程池】已执行完任务数：" + executor.getCompletedTaskCount());*/
    }

    public static class RunnableImpl implements Runnable {
        private Thread t;

        public RunnableImpl(String threadName) {
            if (t == null) {
                if(StringUtils.isEmpty(threadName)){
                    t = new Thread();
                } else {
                    t = new Thread(this, threadName);
                }
            }
        }

        @Override
        public void run() {
            // 让线程睡眠一会
            try {
                System.out.println("Thread " + t.getName() + " is running, 开始执行时间：" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
                Thread.sleep(3000L);
                System.out.println("Thread " + t.getName() + " is running, 执行结束时间：" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
            } catch (InterruptedException e) {
                System.out.println("Thread " + t.getName() + " is InterruptedException");
            }
        }

        public String getThreadName() {
            return t.getName();
        }
    }
}

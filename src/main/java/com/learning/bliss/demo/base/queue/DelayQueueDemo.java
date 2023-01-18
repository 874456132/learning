package com.learning.bliss.demo.base.queue;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2022/5/29 16:36
 * @Version 1.0
 */
public class DelayQueueDemo {

    private static final DelayQueue queue = new DelayQueue();
    private static final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private static final ThreadPoolExecutor threadPoolExecutorOut = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    @SneakyThrows
    public static void main(String[] args) {
        for (int i = 1; i < 10; i++) {
            threadPoolExecutor.execute(new RunnableImpl("thread - " + i));
        }
        for (; ; ) {
            threadPoolExecutorOut.execute(() -> {
                        Delayed consum = null;
                        try {
                            consum = queue.take();
                            if(consum != null){
                                System.out.println(consum + " out queue");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
            );

        }
    }

    static class DelayQueueTask implements Delayed {

        /**
         * 任务名称
         */
        private String taskName;

        /**
         * 入队时间
         */
        private long startTime = System.currentTimeMillis();

        /**
         * 延迟时间 初始化时根据实际情况指定
         */
        private long delayTime;

        DelayQueueTask(String taskName, long delayTime) {
            this.taskName = taskName;
            this.delayTime = delayTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(startTime + (delayTime * 1000) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if ((this.getDelay(TimeUnit.SECONDS) - o.getDelay(TimeUnit.SECONDS)) > 0) {
                return 1;
            } else if ((this.getDelay(TimeUnit.SECONDS) - o.getDelay(TimeUnit.SECONDS)) < 0) {
                return -1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "DelayQueueTask{" +
                    "startTime=" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault())) +
                    ", delayTime=" + delayTime +
                    '}';
        }
    }

    public static class RunnableImpl implements Runnable {
        private Thread t;

        public RunnableImpl(String threadName) {
            if (t == null) {
                if (org.springframework.util.StringUtils.isEmpty(threadName)) {
                    t = new Thread();
                } else {
                    t = new Thread(this, threadName);
                }
            }
        }

        @Override
        public void run() {
            DelayQueueTask delayQueueTask = new DelayQueueTask(t.getName(), RandomUtils.nextLong(1, 10) * 2);
            System.out.println("Thread " + t.getName() + " in queue" + "\t\t" + "开始执行时间：" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + "\t\t" + delayQueueTask);
            queue.offer(delayQueueTask);
        }

        public String getThreadName() {
            return t.getName();
        }
    }
}

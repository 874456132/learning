package com.learning.bliss.config.threadPool;

import com.learning.bliss.handle.redis.CustomStreamErrorHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * 线程池工厂类
 * ThreadPoolExecutor是Java的线程池
 * ThreadPoolTaskExecutor是spring封装的线程池
 *
 * @Author xuexc
 * @Date 2021/7/2 13:52
 * @Version 1.0
 */
@Configuration
public class ThreadPoolExecutorFactory {

    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new LinkedBlockingQueue());

    public static ThreadPoolExecutor threadPoolExecutor1 = new ThreadPoolExecutor(5, 10000, 2, TimeUnit.SECONDS, new LinkedBlockingQueue(10));

    /**
     * 拒绝策略
     * CallerRunsPolicy：如果线程池关闭（执行了shutdown()或shutdownNow()）时丢弃任务，否则该任务由主线程执行。
     * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常，中断调用者的处理过程挺好，同于不指定拒绝策略
     * DiscardPolicy：丢弃无法加载的任务，继续执行后续任务。
     * DiscardOldestPolicy：丢弃队列最前面的任务(最早)，然后重新提交被拒绝的任务。
     */
    public static ThreadPoolExecutor threadPoolExecutor1_1 = new ThreadPoolExecutor(5, 10, 2, TimeUnit.SECONDS, new LinkedBlockingQueue(10), new ThreadPoolExecutor.DiscardOldestPolicy());

    /**
     * ThreadPoolExecutor扩展
     * 1、beforeExecute：线程池中任务运行前执行
     * 2、afterExecute：线程池中任务运行完毕后执行
     * 3、terminated：线程池退出后执行
     */
    public static ThreadPoolExecutor threadPoolExecutor2 = new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue(10), new ThreadPoolExecutor.DiscardOldestPolicy()){
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            System.out.println("Thread " + t.getName() + " is before execute");
        }
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            System.out.println("Thread " + r.toString() + " is after execute");

        }
        @Override
        protected void terminated() {
            System.out.println("Thread threadPool is terminated");
        }
    };

    /**
     * 固定线程的线程池
     * 核心线程=最大线程 无界队列
     */
    public static ThreadPoolExecutor threadPoolExecutor3 = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    /**
     * 单一线程池
     * 核心线程1,无界队列
     */
    public static ExecutorService threadPoolExecutor4 = Executors.newSingleThreadExecutor();

    /**
     * 缓冲线程池（无限扩大的线程池），其实并非真实的缓冲池，而是核心线程数为0，最大等待时间为1分钟，线程数随任务的多少变化。
     * 线程池最大线程数完全依赖于操作系统（或者说JVM）能够创建的最大线程大小
     * SynchronousQueue：这个阻塞队列没有存储空间，这意味着只要有请求到来，就必须要找到一条工作线程处理他，如果当前没有空闲的线程，那么就会再创建一条新的线程。
     * Java 6的并发编程包中的SynchronousQueue是一个没有数据缓冲的BlockingQueue，生产者线程对其的插入操作put必须等待消费者的移除操作take，反过来也一样。
     *
     * 不像ArrayBlockingQueue或LinkedListBlockingQueue，SynchronousQueue内部并没有数据缓存空间，你不能调用peek()方法来看队列中是否有数据元素，因为数据元素只有当你试着取走的时候才可能存在，
     * 不取走而只想偷窥一下是不行的，当然遍历这个队列的操作也是不允许的。队列头元素是第一个排队要插入数据的线程，而不是要交换的数据。数据是在配对的生产者和消费者线程之间直接传递的，并不会将数据缓冲数据到队列中。
     * 可以这样来理解：生产者和消费者互相等待对方，握手，然后一起离开。
     *
     */
    public static ThreadPoolExecutor threadPoolExecutor5 = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    /**
     * 定时线程池
     * 实际是通过DelayedWorkQueue延时队列，控制数据的读取时间来实现线程的执行频率的
     */
    public static ScheduledThreadPoolExecutor threadPoolExecutor6 = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
    public static ScheduledExecutorService threadPoolExecutor6_1 = Executors.newSingleThreadScheduledExecutor();

    /**
     * parallelism(即配置线程池个数)
     * 可以通过java.util.concurrent.ForkJoinPool.common.parallelism进行配置，最大值不能超过MAX_CAP,即32767.
     * 如果没有指定，则默认为处理器个数 Runtime.getRuntime().availableProcessors() - 1.
     *
     * <p>代码指定：System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");
     * jvm参数指定： -Djava.util.concurrent.ForkJoinPool.common.parallelism=8</p>
     */
    public static ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

    /**
     * parallelism(即配置线程池个数)
     * 处理器个数 Runtime.getRuntime().availableProcessors() - 1.
     */
    public static ForkJoinPool forkJoinPoolProcessors = new ForkJoinPool();

    public static ForkJoinPool forkJoinPoolRedisStream = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
            ForkJoinPool.defaultForkJoinWorkerThreadFactory, new CustomStreamErrorHandle(), true);

    @Bean("taskRedisQueueExecutor")
    public TaskExecutor taskRedisQueueExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("task-redis-queue-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        return taskExecutor;
    }

}

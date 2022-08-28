package com.learning.bliss.base.multiThread.atomic;

import lombok.SneakyThrows;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.reflect.Reflection;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongBinaryOperator;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2021/9/14 19:57
 * @Version 1.0
 */
public class AtomicDemo {
    //问题，多线程并发脏读问题
    //问题产生
    /*private static int var = 0;
    public static void add(){
        ++var;
    }
    @SneakyThrows
    public static void main(String[] args) {
        for(int i = 0; i < 2; i++){
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        Thread.sleep(3000L);
        System.out.println(var);
    }*/
    /*问题产生原因分析：
    * 线程1读取到的值和线程2读取到的值相同，就会导致var被多次写为同一个值，产生var最终结果小于2000 */

    //问题解决方法一：使用同步关键字，使得每次只有一个线程访问var
    /*private static int var = 0;
    public static void add(){
        //加锁可以解决线程安全问题 加锁的方式可以是synchronized、Lock类型的锁
        synchronized (AtomicDemo.class){
            ++var;
        }
    }
    @SneakyThrows
    public static void main(String[] args) {
        for(int i = 0; i < 2; i++){
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        Thread.sleep(3000L);
        System.out.println(var);
    }*/
    //问题解决方法二：使用Lock，使得每次只有一个线程访问var
    /*private static int var = 0;
    private static Lock lock = new ReentrantLock();
    public static void add(){
        lock.lock();
        try {
            ++var;
        } finally {
            lock.unlock();
        }
    }
    @SneakyThrows
    public static void main(String[] args) {
        for(int i = 0; i < 2; i++){
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        Thread.sleep(3000L);
        System.out.println(var);
    }*/
    //问题解决方法三：使用CAS重试机制（反射方式，手写CAS）
    private int var = 0;
    private static Unsafe unsafe;
    private static long offset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            Field field1 = AtomicDemo.class.getDeclaredField("var");
            field1.setAccessible(true);
            offset = unsafe.objectFieldOffset(field1);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public void add(){
        int v;
        do {
            v = unsafe.getIntVolatile(this, offset);
        } while (!unsafe.compareAndSwapInt(this, offset, v, v + 1));
    }
    public static void main(String[] args) {
        AtomicDemo atomicDemo = new AtomicDemo();
        for(int i = 0; i < 2; i++){
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicDemo.add();
                }
            }).start();
        }
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(atomicDemo.var);
    }

    /*private static int var = 0;
    private static Unsafe unsafe;
    private static long offset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            Field field1 = AtomicDemo.class.getDeclaredField("var");
            field1.setAccessible(true);
            offset = unsafe.staticFieldOffset(field1);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @SneakyThrows
    public void add(){
        int v;
        do {
            v = unsafe.getInt(AtomicDemo.class, offset);
        } while (!unsafe.compareAndSwapInt(AtomicDemo.class, offset, v, v + 1));
    }
    @SneakyThrows
    public static void main(String[] args) {
        AtomicDemo atomicDemo = new AtomicDemo();
        for(int i = 0; i < 2; i++){
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicDemo.add();
                }
            }).start();
        }
        Thread.sleep(3000L);
        System.out.println(var);
    }*/

    //使用原子服务AtomicInteger
    /*private volatile int var = 0;//必须为非静态的volatile修饰的变量
    private final AtomicInteger atomicInteger = new AtomicInteger(var);
    @SneakyThrows
    public void add(){
        atomicInteger.incrementAndGet();
    }
    @SneakyThrows
    public static void main(String[] args) {
        AtomicDemo atomicDemo = new AtomicDemo();
        for(int i = 0; i < 2; i++){
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicDemo.add();
                }
            }).start();
        }
        Thread.sleep(3000L);
        System.out.println(atomicDemo.atomicInteger.get());
    }*/

    //使用原子服务AtomicIntegerFieldUpdater
    /*private volatile int var = 0;
    private final AtomicIntegerFieldUpdater atomicInteger = AtomicIntegerFieldUpdater.newUpdater(AtomicDemo.class, "var");
    @SneakyThrows
    public void add(){
        atomicInteger.incrementAndGet(this);
    }
    @SneakyThrows
    public static void main(String[] args) {
        AtomicDemo atomicDemo = new AtomicDemo();
        for(int i = 0; i < 2; i++){
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicDemo.add();
                }
            }).start();
        }
        Thread.sleep(3000L);
        System.out.println(atomicDemo.var);
    }*/

    //ABA问题
    /*static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
    //初始化值和版本号
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100, 1);
    public static void main(String[] args) {
        System.out.println("***********************以下是ABA问题的产生**********************");
        System.out.println("初始值：" + atomicReference.get());
        new Thread(() -> {
            System.out.println("线程" + Thread.currentThread().getName() + "刚开始读取到的值：" + atomicReference.get());
            atomicReference.compareAndSet(100, 101); //100 101 100 即ABA问题，先改为101，然后再改为100
            System.out.println("线程" + Thread.currentThread().getName() + "第一次修改后的值：" + atomicReference.get());
            atomicReference.compareAndSet(101, 100);
            System.out.println("线程" + Thread.currentThread().getName() + "第二次修改后的值：" + atomicReference.get());
        }, "t1").start();

        new Thread(() -> {
            //暂停1秒钟t2线程，保证上面的t1线程完成了一次ABA操作
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程" + Thread.currentThread().getName() + "刚开始读取到的值：" + atomicReference.get());
            atomicReference.compareAndSet(100, 2019);
            System.out.println("线程" + Thread.currentThread().getName() + "修改后的值：" + atomicReference.get());
        }, "t2").start();

        //暂停一会儿线程
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("***********************以下是ABA问题的解决**********************");

        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t第1次版本号: " + stamp);
            //暂停1秒钟t3线程，让t4线程获取相同的版本号，同一起点
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //如果期望的值是100，版本号是atomicStampedReference.getStamp()，就让版本号加1，值改为101
            atomicStampedReference.compareAndSet(100, 101, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t第2次版本号: " + atomicStampedReference.getStamp());
            atomicStampedReference.compareAndSet(101, 100, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t第3次版本号: " + atomicStampedReference.getStamp());
        }, "t3").start();

        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t第1次版本号: " + stamp);
            //暂停3秒钟t4线程，保证上面的t3线程完成了一次ABA操作
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean result = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + "\t修改成功否: " + result + "\t当前操作版本号: " + stamp + "\t当前最新实际版本号: " + atomicStampedReference.getStamp());
            System.out.println(Thread.currentThread().getName() + "\t当前实际最新值: " + atomicStampedReference.getReference());
        }, "t4").start();
    }*/

    //synchronized、AtomicInteger、LongAdder三种方式的效率比较
    /*private volatile int var = 0;
    private static final LongAdder longAdder = new LongAdder();
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    public void add(){
        //加锁可以解决线程安全问题 加锁的方式可以是synchronized、Lock类型的锁
        synchronized (this){
            ++var;
        }
    }
    @SneakyThrows
    public static void main(String[] args) {
        AtomicDemo atomicDemo = new AtomicDemo();
        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 1000) {
                    atomicDemo.add();
                }
                long endTime = System.currentTimeMillis();
                System.out.println("synchronized thread spend: " + (endTime - startTime) + "ms" + "\tvalue= " + atomicDemo.var);
            }).start();
        }
        Thread.sleep(8000L);

        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 1000) {
                    atomicInteger.incrementAndGet();
                }
                long endTime = System.currentTimeMillis();
                System.out.println("synchronized thread spend: " + (endTime - startTime) + "ms" + "\tvalue= " + atomicInteger.get());
            }).start();
        }
        Thread.sleep(8000L);

        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                long startTime = System.currentTimeMillis();
                longAdder.add(0);
                while (System.currentTimeMillis() - startTime < 1000) {
                    longAdder.increment();
                }
                long endTime = System.currentTimeMillis();
                System.out.println("LongAdder thread spend: " + (endTime - startTime) + "ms" + "\tvalue= " + longAdder.sum());
            }).start();
        }
    }*/

    //LongAdder的增强版  可以自定义运算方法 LongAccumulator

    /*private static final LongAccumulator longAccumulator = new LongAccumulator(new LongBinaryOperator() {
        @Override
        public long applyAsLong(long left, long right) {
            return left > right ? 0 : 1;
        }
    }, 0);

    @SneakyThrows
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            new Thread(() -> {
                longAccumulator.accumulate(finalI);
            }).start();
        }
        Thread.sleep(1000L);
        System.out.println(longAccumulator.longValue());
    }*/
}

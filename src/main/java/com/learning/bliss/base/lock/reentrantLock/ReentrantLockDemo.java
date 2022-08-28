package com.learning.bliss.base.lock.reentrantLock;

import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 手写可重入锁
 * 需要考虑的点：1、独占锁 2、同一个线程再次获取锁时
 * @Author xuexc
 * @Date 2021/9/22 19:55
 * @Version 1.0
 */
public class ReentrantLockDemo implements Lock, Serializable {

    private static final long serialVersionUID = -5728373598851491612L;

    /*自定义的AQS对象，以实现各种同步功能*/
    private final Sync sync;

    public ReentrantLockDemo() {
        this(false);
    }

    public ReentrantLockDemo(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

    @Override
    public void lock() {
        sync.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return isFair() ? sync.fairTryAcquire(1) : sync.nonfairTryAcquire(1);
    }

    /**
     * 在unit时间段内尝试获取锁
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    /**
     * 是否公平锁
     * @return
     */
    public final boolean isFair() {
        return sync instanceof ReentrantLockDemo.FairSync;
    }

    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    public final Collection<Thread> getQueuedThreads(){
        return sync.getQueuedThreads();
    }

    /**
     * 获取当前线程持有重入锁的数目
     * @return
     */
    final int getHoldCount() {
        return sync.getHoldCount();
    }

    /**
     * 当前锁是否被占用
     * @return
     */
    public boolean isLocked() {
        return sync.isLocked();
    }


    /**
     * 公平锁的队列同步器
     */
    private static class FairSync extends Sync {

        @Override
        final void lock() {
            acquire(1);//1表示重入锁的获取次数
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return fairTryAcquire(1);
        }
    }

    /**
     * 非公平锁的队列同步器
     */
    private static class NonfairSync extends Sync {

        @Override
        final void lock() {
            //先争抢一次锁 当锁抢到时，则将当前线程赋值给锁的持有线程
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                //进入获取锁流程中,当获取失败时，加入等待队列
                acquire(1);
        }

        @Override
        protected boolean tryAcquire(int arg) {
            return nonfairTryAcquire(arg);
        }
    }

    /**
     * 自定义的AQS对象，以定义资源的占用以及释放规则
     * 通过state、exclusiveOwnerThread实现
     */
    private abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -3582250905925928428L;

        abstract void lock();

        /**
         * 非公平锁尝试获取
         * @param acquires
         * @return
         */
        @SneakyThrows
        final boolean nonfairTryAcquire(int acquires) {
            Thread t = Thread.currentThread();
            int lockState = getState();//当前锁状态
            if (lockState == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(t);
                    return true;
                }
            } else {
                if (t == getExclusiveOwnerThread()) {
                    int lockStateNew = lockState + acquires;
                    if (lockStateNew < 0) throw new RuntimeException("当前线程获取重入锁的次数超限");
                    setState(lockStateNew);//同一个线程内是同步的，不需要再使用CAS更新state
                    return true;
                }
            }
            return false;
        }

        /**
         * 公平锁尝试获取
         * @param acquires
         * @return
         */
        final boolean fairTryAcquire(int acquires) {
            Thread t = Thread.currentThread();
            int lockState = getState();//当前锁状态
            if (lockState == 0) {
                //公平重入锁根据队列顺序去获取锁  hasQueuedPredecessors(当前节点前是否存在等待的节点)
                if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(t);
                    return true;
                }
            } else {
                //重入锁
                if (t == getExclusiveOwnerThread()) {
                    int lockStateNew = lockState + acquires;
                    if (lockStateNew < 0) throw new RuntimeException("当前线程获取重入锁的次数超限");
                    setState(lockStateNew);//同一个线程内是同步的，不需要再使用CAS更新state
                    return true;
                }
            }
            return false;
        }

        /**
         * 释放资源
         * @param arg
         * @return
         */
        @Override
        protected boolean tryRelease(int arg) {
            int lockState = getState() - arg;
            if (Thread.currentThread() != getExclusiveOwnerThread()) throw new IllegalMonitorStateException();
            boolean free = false;
            if (lockState == 0) {
                free = true;
                setExclusiveOwnerThread(null);//释放完成，将独占锁的持有线程清空
            }
            setState(lockState);
            return free;
        }

        final ConditionObject newCondition(){
            return new ConditionObject();
        }

        /**
         * 当前线程是否拥有独占锁
         * @return
         */
        @Override
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        /**
         * 获取当前锁的持有者
         * @return
         */
        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        /**
         * 获取当前线程持有重入锁的数目
         * @return
         */
        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        /**
         * 当前锁是否被占用
         * @return
         */
        final boolean isLocked() {
            return getState() != 0;
        }
    }

}

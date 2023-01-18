package com.learning.bliss.demo.base.lock.reentrantReadWriteLock;

import sun.misc.Unsafe;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * 手写读写锁
 *
 * @Author xuexc
 * @Date 2021/11/15 19:09
 * @Version 1.0
 */
public class ReentrantReadWriteLockDemo implements ReadWriteLock, Serializable {
    private static final long serialVersionUID = 2300367532759308329L;

    private final Sync sync;
    private final ReadLock readerLock;
    private final WriteLock writerLock;

    public ReentrantReadWriteLockDemo() {
        this(false);
    }

    public ReentrantReadWriteLockDemo(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
        readerLock = new ReadLock(this);//通过this将sync对象传递至ReadLock.sync
        writerLock = new WriteLock(this);
    }

    @Override
    public ReadLock readLock() {
        return readerLock;
    }

    @Override
    public WriteLock writeLock() {
        return writerLock;
    }

    public static class ReadLock implements Lock, Serializable {
        private static final long serialVersionUID = 3714877556165644943L;
        private final Sync sync;

        public ReadLock(ReentrantReadWriteLockDemo lock) {
            sync = lock.sync;
        }

        @Override
        public void lock() {
            sync.acquireShared(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            sync.acquireSharedInterruptibly(1);
        }

        @Override
        public boolean tryLock() {
            return sync.tryReadLock();
        }

        @Override
        public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
            return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
        }

        @Override
        public void unlock() {
            sync.releaseShared(1);
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            int r = sync.getReadLockCount();
            return super.toString() +
                    "[Read locks = " + r + "]";
        }
    }

    public static class WriteLock implements Lock, Serializable {

        private static final long serialVersionUID = 6604591159505495552L;

        private final Sync sync;

        public WriteLock(ReentrantReadWriteLockDemo lock) {
            sync = lock.sync;
        }

        @Override
        public void lock() {
            sync.acquire(1);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void unlock() {
            sync.release(1);
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }

    /**
     * 公平锁的队列同步器
     */
    private static class FairSync extends Sync {
        private static final long serialVersionUID = -3619840765641792031L;

        /**
         * 是否存在其他等待的线程
         *
         * @return
         */
        @Override
        final boolean readerShouldBlock() {
            return hasQueuedPredecessors();
        }

        /**
         * 是否存在其他等待的线程
         *
         * @return
         */
        @Override
        final boolean writerShouldBlock() {
            return hasQueuedPredecessors();
        }
    }

    /**
     * 非公平锁的队列同步器
     */
    static class NonfairSync extends Sync {

        private static final long serialVersionUID = 6040225707176570355L;
        /**
         * 读是否应该阻塞（当存在写锁竞争或者写锁独占时（当当前队列中第一个等待节点为独占时），则应该阻塞）
         *
         * @return
         */
        @Override
        final boolean readerShouldBlock() {//读是否应该阻塞（当存在写锁竞争或者写锁独占时（当当前队列中第一个等待节点为独占时），则应该阻塞）
            /* As a heuristic to avoid indefinite writer starvation,
             * block if the thread that momentarily appears to be head
             * of queue, if one exists, is a waiting writer.  This is
             * only a probabilistic effect since a new reader will not
             * block if there is a waiting writer behind other enabled
             * readers that have not yet drained from the queue.
             */
            //FIXME 有问题
            boolean flag = true;
            try {
                Method method = AbstractQueuedSynchronizer.class.getDeclaredMethod("apparentlyFirstQueuedIsExclusive");
                method.setAccessible(true);
                flag = (boolean)method.invoke(this);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return flag;
        }

        /**
         * 写锁是否应该阻塞（写锁总是可以直接获取资源，进行写操作）
         * @return
         */
        @Override
        final boolean writerShouldBlock() {
            return false;
        }
    }

    private abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 6093005452719798504L;

        static final int SHARED_SHIFT = 16;
        static final int SHARED_UNIT = (1 << SHARED_SHIFT);
        static final int MAX_COUNT = (1 << SHARED_SHIFT) - 1;
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        //0
        static int sharedCount(int c) {
            return c >>> SHARED_SHIFT;
        }

        //c
        static int exclusiveCount(int c) {
            return c & EXCLUSIVE_MASK;
        }

        private transient ThreadLocalHoldCounter readHolds;
        private transient HoldCounter cachedHoldCounter;

        private transient Thread firstReader = null;
        private transient int firstReaderHoldCount;

        abstract boolean readerShouldBlock();

        abstract boolean writerShouldBlock();

        Sync() {
            readHolds = new ThreadLocalHoldCounter();
            setState(getState()); // ensures visibility of readHolds
        }

        /*******************************************************************
         ***************************** shard *******************************
         *******************************************************************/
        /**
         * 共享资源获取
         *
         * @param unused
         * @return
         */
        @Override
        protected final int tryAcquireShared(int unused) {

            Thread current = Thread.currentThread();
            int c = getState();
            //写锁被占用，且非当前线程，拒绝
            if (exclusiveCount(c) != 0 &&
                    getExclusiveOwnerThread() != current)
                return -1;
            int r = sharedCount(c);
            //写锁未被占用，则获取锁（修改state）,如果当前资源未被占用，则赋值第一个等待线程（firstReader），并设置共享资源占用次数，
            //                               如果第一个等待线程（firstReader）是当前线程，则资源占用次数自增1
            //                               否则  当最后一个线程计数器为空，则新建一个 HoldCounter 对象
            //                                     如果最后一个线程计数器为初始化对象（rh.count == 0），就将上个线程的 HoldCounter 覆盖本地的，且资源占用次数自增1
            if (!readerShouldBlock() &&
                    r < MAX_COUNT &&
                    compareAndSetState(c, c + SHARED_UNIT)) {
                if (r == 0) {
                    firstReader = current;
                    firstReaderHoldCount = 1;
                } else if (firstReader == current) {
                    firstReaderHoldCount++;
                } else {
                    HoldCounter rh = cachedHoldCounter;
                    //如果最后一个线程计数器是 null 或者不是当前线程，那么就新建一个 HoldCounter 对象
                    if (rh == null || rh.tid != getThreadId(current))
                        cachedHoldCounter = rh = readHolds.get();
                    else if (rh.count == 0)
                        readHolds.set(rh);
                    rh.count++;
                }
                return 1;
            }
            return fullTryAcquireShared(current);
        }

        final int fullTryAcquireShared(Thread current) {
            /*
             * This code is in part redundant with that in
             * tryAcquireShared but is simpler overall by not
             * complicating tryAcquireShared with interactions between
             * retries and lazily reading hold counts.
             */
            HoldCounter rh = null;
            for (;;) {
                int c = getState();
                if (exclusiveCount(c) != 0) {
                    if (getExclusiveOwnerThread() != current)
                        return -1;
                    // else we hold the exclusive lock; blocking here
                    // would cause deadlock.
                } else if (readerShouldBlock()) {
                    // Make sure we're not acquiring read lock reentrantly
                    if (firstReader == current) {
                        // assert firstReaderHoldCount > 0;
                    } else {
                        if (rh == null) {
                            rh = cachedHoldCounter;
                            if (rh == null || rh.tid != getThreadId(current)) {
                                rh = readHolds.get();
                                if (rh.count == 0)
                                    readHolds.remove();
                            }
                        }
                        if (rh.count == 0)
                            return -1;
                    }
                }
                if (sharedCount(c) == MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                if (compareAndSetState(c, c + SHARED_UNIT)) {
                    if (sharedCount(c) == 0) {
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    } else if (firstReader == current) {
                        firstReaderHoldCount++;
                    } else {
                        if (rh == null)
                            rh = cachedHoldCounter;
                        if (rh == null || rh.tid != getThreadId(current))
                            rh = readHolds.get();
                        else if (rh.count == 0)
                            readHolds.set(rh);
                        rh.count++;
                        cachedHoldCounter = rh; // cache for release
                    }
                    return 1;
                }
            }
        }

        /**
         * 获取共享锁
         * 1、当存在独占锁被占用时，且非本线程，则返回false
         * 2、当共享锁占用数量达到最大时，则返回false
         * 3、CAS修改state,修改成功后赋值firstReader、firstReaderHoldCount
         * @return
         */
        final boolean tryReadLock() {
            Thread current = Thread.currentThread();
            for (;;) {
                int c = getState();
                if (exclusiveCount(c) != 0 &&
                        getExclusiveOwnerThread() != current)
                    return false;
                int r = sharedCount(c);
                if (r == MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                if (compareAndSetState(c, c + SHARED_UNIT)) {
                    if (r == 0) {
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    } else if (firstReader == current) {
                        firstReaderHoldCount++;
                    } else {
                        HoldCounter rh = cachedHoldCounter;
                        if (rh == null || rh.tid != getThreadId(current))
                            cachedHoldCounter = rh = readHolds.get();
                        else if (rh.count == 0)
                            readHolds.set(rh);
                        rh.count++;
                    }
                    return true;
                }
            }
        }

        /**
         * 共享资源释放
         *
         * @param unused
         * @return
         */
        protected final boolean tryReleaseShared(int unused) {
            Thread current = Thread.currentThread();
            if (firstReader == current) {
                // assert firstReaderHoldCount > 0;
                if (firstReaderHoldCount == 1)
                    firstReader = null;
                else
                    firstReaderHoldCount--;
            } else {
                HoldCounter rh = cachedHoldCounter;
                if (rh == null || rh.tid != getThreadId(current))
                    rh = readHolds.get();
                int count = rh.count;
                if (count <= 1) {
                    readHolds.remove();
                    if (count <= 0)
                        throw unmatchedUnlockException();
                }
                --rh.count;
            }
            for (;;) {
                int c = getState();
                int nextc = c - SHARED_UNIT;
                if (compareAndSetState(c, nextc))
                    // Releasing the read lock has no effect on readers,
                    // but it may allow waiting writers to proceed if
                    // both read and write locks are now free.
                    return nextc == 0;
            }
        }

        private final int getReadLockCount() {
            return sharedCount(getState());
        }


        /*******************************************************************
         **************************** exclusive ****************************
         *******************************************************************/
        /**
         * 独占资源获取
         *
         * @param acquires
         * @return
         */
        protected final boolean tryAcquire(int acquires) {
            /**
             * 当前锁被占用时，getState() != 0
             *      1、读锁被占用 c != 0 && w == 0，则返回false
             *      2、写锁被占用w != 0，如果当前锁的持有线程非本线程，返回false，否则当未溢出时，则更新state
             * 当前锁未被占用时，获取写锁是否被阻塞，当不被阻塞时，则更新state,并将锁的持有线程更新为本线程
             */
            Thread current = Thread.currentThread();
            int c = getState();//锁占用的总次数
            int w = exclusiveCount(c);//写锁占用的次数
            if (c != 0) {
                // w = 0 说明已经有线程获取了读锁, w != 0并且当前线程不是写锁拥有者, 则返回false
                if (w == 0 || current != getExclusiveOwnerThread())
                    return false;
                if (w + exclusiveCount(acquires) > MAX_COUNT)
                    throw new Error("Maximum lock count exceeded");
                // Reentrant acquire
                setState(c + acquires);
                return true;
            }
            if (writerShouldBlock() ||
                    !compareAndSetState(c, c + acquires))
                return false;
            setExclusiveOwnerThread(current);
            return true;
        }

        /**
         * 独占资源释放
         *
         * @param releases
         * @return
         */
        protected final boolean tryRelease(int releases) {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            int nextc = getState() - releases;
            boolean free = exclusiveCount(nextc) == 0;
            if (free)
                setExclusiveOwnerThread(null);
            setState(nextc);
            return free;
        }

        @Override
        protected boolean isHeldExclusively(){
            return getExclusiveOwnerThread() == Thread.currentThread();
        }
        private static final class HoldCounter {
            int count = 0;
            final long tid = getThreadId(Thread.currentThread());
        }

        private static final class ThreadLocalHoldCounter extends ThreadLocal<Sync.HoldCounter> {
            public Sync.HoldCounter initialValue() {
                return new Sync.HoldCounter();
            }
        }

        /**
         * 写锁是否占用
         * @return
         */
        final boolean isWriteLocked() {
            return exclusiveCount(getState()) != 0;
        }

        private static final long getThreadId(Thread thread) {
            return unsafe.getLongVolatile(thread, offset);
        }

        private IllegalMonitorStateException unmatchedUnlockException() {
            return new IllegalMonitorStateException(
                    "attempt to unlock read lock, not locked by current thread");
        }

        private static Unsafe unsafe;
        private static long offset;

        static {
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafe = (Unsafe) field.get(null);
                Field field1 = Thread.class.getDeclaredField("tid");
                field1.setAccessible(true);
                offset = unsafe.objectFieldOffset(field1);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //当服务启动后可以使用下面的方法
            /*try {
                unsafe = sun.misc.Unsafe.getUnsafe();
                Class<?> tk = Thread.class;
                offset = unsafe.objectFieldOffset
                        (tk.getDeclaredField("tid"));
            } catch (Exception e) {
                throw new Error(e);
            }*/
        }
    }
}

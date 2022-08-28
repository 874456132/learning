package com.learning.bliss.base.multiThread.threadLocal;


import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ThreadLocal
 *
 * @Author xuexc
 * @Date 2021/7/13 18:34
 * @Version 1.0
 */
public class ThreadLocalDemo {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100000; i++) {
            int finalI = i;
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    //Thread.sleep(5000);
                    HashMap map = new HashMap();
                    for (int i = 0; i < 10000; i++){
                        map.put(i,"张三");
                        map.put(i+"aa","张三");
                    }
                    ThreadLocalHolder.set(map);
                    //ThreadLocalHolder.set("当前线程 " + Thread.currentThread().getName() + " use " + ThreadLocalHolder.getHash() + " into ThreadLocal entry");
                    //ThreadLocalHolder.clean();
                    /*Thread.sleep(100);
                    System.out.println("当前线程 " + Thread.currentThread().getName() + " entry,key is: " + ThreadLocalHolder.getHash() + "==========value is: " + ThreadLocalHolder.get());

                    Object myMapReturn = ThreadLocalHolder.getMap(Thread.currentThread());
                    Object threadLocalsReturn  = ThreadLocalHolder.getThreadLocals(Thread.currentThread());
                    System.out.println("当前线程 " + Thread.currentThread().getName() +" ThreadLocal.getMap()：" + myMapReturn.toString() + "==========" + JSONObject.toJSONString(myMapReturn));
                    System.out.println("当前线程 " + Thread.currentThread().getName() +" ThreadLocal.getMap()：" +"Thread.threadLocals属性：" + threadLocalsReturn.toString() + "==========" + JSONObject.toJSONString(threadLocalsReturn));
                    */
                    if (finalI == 99999){
                        System.out.println("usedMemory:" + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
                    }
                }
            });
        }
        //Thread.sleep(1000);
        ThreadLocalHolder.set("当前线程 " + Thread.currentThread().getName() + " use " + ThreadLocalHolder.getHash() + " into ThreadLocal entry");
        Thread.sleep(100);
        System.out.println("当前线程 " + Thread.currentThread().getName() + " entry,key is: " + ThreadLocalHolder.getHash() + "==========value is: " + ThreadLocalHolder.get());
        Object myMapReturn = ThreadLocalHolder.getMap(Thread.currentThread());
        Object threadLocalsReturn  = ThreadLocalHolder.getThreadLocals(Thread.currentThread());
        System.out.println("当前线程 " + Thread.currentThread().getName() +" ThreadLocal.getMap()：" + myMapReturn.toString() + "==========" + JSONObject.toJSONString(myMapReturn));
        System.out.println("当前线程 " + Thread.currentThread().getName() +" ThreadLocal.getMap()：" +"Thread.threadLocals属性：" + threadLocalsReturn.toString() + "==========" + JSONObject.toJSONString(threadLocalsReturn));
        System.out.println("最后main  usedMemory:" + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
        //ThreadLocalHolder.clean();
        //关闭线程池
        while (threadPoolExecutor.getActiveCount() > 0) {
            Thread.sleep(1000);
        }
        threadPoolExecutor.shutdownNow();
    }
}

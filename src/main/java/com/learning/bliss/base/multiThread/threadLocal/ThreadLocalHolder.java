package com.learning.bliss.base.multiThread.threadLocal;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2021/7/23 10:56
 * @Version 1.0
 */
public final class ThreadLocalHolder {

    private static ThreadLocal<Object> holder = new ThreadLocal<>();

    static {
        System.out.println("初始化时的ThreadLocal对象：" + holder.toString() + "-->" + JSONObject.toJSONString(holder));
        Object myMapReturn = ThreadLocalHolder.getMap(Thread.currentThread());
        Object threadLocalsReturn  = ThreadLocalHolder.getThreadLocals(Thread.currentThread());

        System.out.println("初始化时的ThreadLocal.getMap()：" + myMapReturn.toString() + "==========" + JSONObject.toJSONString(myMapReturn));
        System.out.println("初始化时的Thread.threadLocals属性：" + threadLocalsReturn.toString() + "==========" + JSONObject.toJSONString(threadLocalsReturn));
        System.out.println("");
    }

    public ThreadLocalHolder() {
    }

    public static void set(Object value) {
        holder.set(value);
    }

    public static Object get() {
        return holder.get();
    }

    public static void clean() {
        holder.remove();
    }

    public static String getHash() {
        return holder.toString();
    }
    public static Object getThreadLocals(Thread t) {
        Class innerClazz[] = ThreadLocal.class.getDeclaredClasses();
        for(Class innerClass : innerClazz){
            System.out.println(innerClass.getSimpleName());
            if("ThreadLocalMap".equals(innerClass.getSimpleName())){
                try {
                    Field threadLocals = innerClass.getDeclaredField("threadLocals");
                    threadLocals.setAccessible(true);
                    return threadLocals.get(t);

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Object getMap(Thread t) {
        Method m = null;
        try {
            m = holder.getClass().getDeclaredMethod("getMap", Thread.class);
            return m.invoke(holder, Thread.currentThread());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}

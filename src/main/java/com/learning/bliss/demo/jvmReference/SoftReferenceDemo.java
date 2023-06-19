package com.learning.bliss.demo.jvmReference;

import java.lang.ref.SoftReference;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/3/21 21:14
 * @Version 1.0
 */
public class SoftReferenceDemo {
    public static void main(String[] args) {
        Object obj = new Object();
        SoftReference sf = new SoftReference(obj);
        System.out.println("obj   " + obj);
        System.out.println("sf     " + sf.get());
        obj = null;
        System.out.println("obj   " + obj);
        //有时候会返回null
        System.out.println("sf   " + sf.get());
    }
}

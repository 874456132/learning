package com.learning.bliss.base.array;

/**
 * 有些处理器（尤其是早期的Alphas处理器）没有提供写单个字节的功能。在这样的处理器上更新byte数组，若只是简单的读取整个内容，
 * 更新对应的字节，然后将整个内容再写回内存，将是不合法的。
 * 目前常用的处理器已经没有这种问题
 *
 * @Author xuexc
 * @Date 2021/9/14 17:20
 * @Version 1.0
 */
public class WordTearing extends Thread {
    static final int LENGTH = 8;
    static final int ITERS  = 1000000;
    static byte[] counts    = new byte[LENGTH];
    static Thread[] threads = new Thread[LENGTH];

    final int id;
    WordTearing(int i) {
        id = i;
    }

    public void run() {
        byte v = 0;
        for (int i = 0; i < ITERS; i++) {
            byte v2 = counts[id];
            if (v != v2) {
                System.err.println("Word-Tearing found: " +
                        "counts[" + id + "] = "+ v2 +
                        ", should be " + v);
                return;
            }
            v++;
            counts[id] = v;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < LENGTH; ++i)
            (threads[i] = new WordTearing(i)).start();
    }
}
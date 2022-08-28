package com.learning.bliss.base.multiThread.communication;

/**
 * 线程通信
 *
 * @Author: xuexc
 * @Date: 2021/1/3 17:04
 * @Version 0.1
 */
public class ThreadCommunicationMain {

    public String aa;
    private String bb;
    public static void main(String[] args) {
        System.out.println("============= 文件通信（存储共享） =============");
        /*Thread thread1 = new Thread(() -> {
            try {
                while (true) {
                    ;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/
        String a = new ThreadCommunicationMain().bb;

    }
}


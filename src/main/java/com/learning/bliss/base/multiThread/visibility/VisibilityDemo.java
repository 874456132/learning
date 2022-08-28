package com.learning.bliss.base.multiThread.visibility;

import lombok.SneakyThrows;

/**
 * 线程安全之可见性
 *
 * @Author xuexc
 * @Date 2021/8/10 16:12
 * @Version 1.0
 */
public class VisibilityDemo {
    private volatile boolean flag = true;

    public static void main(String[] args) {
        VisibilityDemo demo = new VisibilityDemo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                /*问题产生的原因
                1、指令重排序：class -> 运行时jit编译 -> 汇编指令 -> 指令重排序，导致在真正的执行过程中，while中的判断条件，只执行了一次
                2、因两个线程，子线程在运行时拿到的flag还是初始的值true，所以后面再更改flag值，都不影响子线程。*/
                /*解决方法：
                1、使用synchronized关键字
                2、使用对象锁Lock
                3、使用volatile关键字（强制读取主内存）*/

                /**
                 *
                 * 设置运行模式：jre/bin/server 放置hsdis动态链接库
                 *      增加虚拟机运行参数，将服务运行模式设置为-server，变成死循环，默认运行模式（-client）,正常
                 * 打印出jit日志：通过设置jvm参数，打印出jit编译的内容（非class文件），通过可视化工具jitwatch进行查看
                 *      -server -XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation -XX:+PrintAssembly -XX:LogFile=jit.log
                 * 关闭jit优化：-Djava.compiler=NONE
                 */
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (demo.flag){
                    i++;
                }
                System.out.println(i);
            }
        }).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        demo.flag = false;
        System.out.println("flag被设置为false了");
    }
}

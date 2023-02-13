package com.learning.bliss.demo.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/2/13 12:30
 * @Version 1.0
 */
public class UsingMultipleReactors {

    public ServerSocketChannel serverSocketChannel;
    // 用来接收客户端连接 数组其实只分配空间，不创建MainReactor对象，所以要遍历创建
    public MainReactor[] mainReactors = new MainReactor[1];
    // 用来处理客户端连接的读取、写入
    public SubReactor[] subReactors = new SubReactor[10];
    /** 线程池，用来处理客户端连接后的业务逻辑 */
    public ExecutorService workThreadPool = Executors.newCachedThreadPool();

    int port;

    UsingMultipleReactors(int port){
        this.port = port;
    }

    /**
     * 初始化服务端
     */
    public void init() throws IOException {
        //创建一个服务端通道
        serverSocketChannel = ServerSocketChannel.open();
        //设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //注册到mainReactor-Thread
        int index = new Random().nextInt(mainReactors.length);
        mainReactors[index].register(serverSocketChannel, subReactors, workThreadPool);
        //启动mainReactor-Thread线程
        mainReactors[index].start();
    }

    /**
     * 服务端绑定端口
     */
    public void bind() throws IOException {
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("服务端启动成功");
    }

    /**
     * 初始化MainReactor和SubReactor
     */
    public void initAndRegister() throws IOException {
        for (int i = 0; i < mainReactors.length; i++) {
            mainReactors[i] = new MainReactor();
        }

        for (int i = 0; i < subReactors.length; i++) {
            subReactors[i] = new SubReactor();
        }
    }

}

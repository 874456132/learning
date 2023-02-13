package com.learning.bliss.demo.io.nio;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * mainReactor-Thread
 * 接收客户端连接，然后交给Acceptor处理
 */
class MainReactor extends Thread {
    //创建一个Selector
    private Selector selector;

    //用来处理客户端连接的读取、写入
    private SubReactor[] subReactors;
    private ExecutorService workThreadPool;

    AtomicInteger integer = new AtomicInteger(0);

    public MainReactor() throws IOException {
        selector = Selector.open();
    }

    @Override
    public void run() {
        while (true) {
            try {
                //启动Selector
                selector.select();
                //获取事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                //遍历事件
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()) {
                        //获取客户端通道
                        SocketChannel socketChannel = ((ServerSocketChannel)selectionKey.channel()).accept();
                        Acceptor acceptor = new Acceptor();
                        //把客户端通道交给Acceptor去处理
                        acceptor.register(socketChannel);
                    }
                    //处理完之后要移除
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(ServerSocketChannel serverSocketChannel, SubReactor[] subReactors, ExecutorService workThreadPool) throws ClosedChannelException {
        //注册OP_ACCEPT事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        this.subReactors = subReactors;
        this.workThreadPool = workThreadPool;
    }

    /**
     *  处理客户端的连接
     *  给每个客户端连接分配一个subReactor-Thread
     */
    class Acceptor {

        public void register(SocketChannel socketChannel) throws IOException {
            //设置为非阻塞模式
            socketChannel.configureBlocking(false);
            int index = integer.getAndIncrement() % subReactors.length;
            SubReactor subReactor = subReactors[index];
            //给客户端连接分配一个subReactor线程
            subReactor.register(socketChannel, workThreadPool);
            //启动subReactor线程
            subReactor.start();
            System.out.println("收到新连接：" + socketChannel.getRemoteAddress());
        }
    }
}


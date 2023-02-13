package com.learning.bliss.demo.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * 一个线程负责多个客户端连接
 * 从channel中读数据、写数据
 */
class SubReactor extends Thread {

    //创建一个Selector
    private Selector selector;
    //用于判断SubReactor线程是否已经启动
    private volatile boolean isRunning = false;

    private ExecutorService workThreadPool;

    public SubReactor() throws IOException {
        selector = Selector.open();
    }

    @Override
    public void start() {
        //判断SubReactor线程是否已经启动
        //如果没有启动，就启动SubReactor线程
        if (!isRunning) {
            isRunning = true;
            super.start();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                //启动Selector
                selector.select();
                //获取事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                //遍历事件
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable()) {
                        try {
                            SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                            new Handler(socketChannel);
                        } catch (Exception e) {
                            e.printStackTrace();
                            selectionKey.cancel();
                        }
                    }
                    //处理完之后要移除
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(SocketChannel socketChannel, ExecutorService workThreadPool) throws IOException {
        //注册OP_READ事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        this.workThreadPool = workThreadPool;
    }

    /** 读取或者写入数据 */
    class Handler {
        //用来读取或者写入数据
        public Handler(SocketChannel socketChannel) throws IOException {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            while (socketChannel.isOpen() && socketChannel.read(readBuffer) != -1) {
                //如果有数据可读，简单的判断一下大于0
                if (readBuffer.position() > 0) {
                    break;
                }
            }
            //没有数据可读，就直接返回
            if (readBuffer.position() == 0) {
                return;
            }
            //转换为读取模式
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.limit()];
            readBuffer.get(bytes);
            System.out.println("获取到新的数据：" + new String(bytes));
            System.out.println("获取到新的数据，来自：" + socketChannel.getRemoteAddress());
            //线程池，用来处理业务数据
            workThreadPool.execute(() -> {

            });

            //向客户端写数据
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 11\r\n\r\n" +
                    "hello world";
            ByteBuffer writeBuffer = ByteBuffer.wrap(response.getBytes());
            while (writeBuffer.hasRemaining()) {
                socketChannel.write(writeBuffer);
            }
        }
    }
}
package com.learning.bliss.demo.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/2/12 17:15
 * @Version 1.0
 */
public class NIOClient {
    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8080));
            socketChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.print("请输入:");
                String msg = scanner.nextLine();
                buffer.put(msg.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
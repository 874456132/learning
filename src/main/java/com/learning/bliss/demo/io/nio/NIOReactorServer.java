package com.learning.bliss.demo.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/2/12 22:36
 * @Version 1.0
 */
public class NIOReactorServer {
    static final int port = 8080;

    public static void main(String[] args) throws IOException {
        UsingMultipleReactors nioReactor = new UsingMultipleReactors(port);
        nioReactor.initAndRegister();
        nioReactor.init();
        nioReactor.bind();
    }
}

package com.learning.bliss.demo.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/2/10 17:23
 * @Version 1.0
 */
public class BIOServer {
    /*public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务器启动成功");
        while (!serverSocket.isClosed()) {
            Socket request = serverSocket.accept(); // 阻塞
            System.out.println("收到新连接：" + request);
            try {
                InputStream inputStream = request.getInputStream(); // 获取数据流
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String message = "";
                String read;
                while ((read = bufferedReader.readLine()) != null) {
                    message += read;
                    if (message.length() == 0) {
                        break;
                    }
                }
                System.out.println("收到数据：{" + message + "}，来自：" + request);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                request.close();
            }
        }
        serverSocket.close();
    }*/
    //多线程实现任务处理
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务器启动成功");
        while (!serverSocket.isClosed()) {
            Socket request = serverSocket.accept();
            System.out.println("收到新连接：" + request.toString());
            // 多线程接收多个连接
            executorService.submit(
                    () -> {
                        try {
                            InputStream inputStream = request.getInputStream();
                            BufferedReader bufferedReader =
                                    new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                            String message = "";
                            String read;
                            while ((read = bufferedReader.readLine()) != null) {
                                message += read;
                                if (message.length() == 0) {
                                    break;
                                }
                            }
                            System.out.println("收到数据：{" + message + "}，来自：" + request);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                request.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        serverSocket.close();
    }
}

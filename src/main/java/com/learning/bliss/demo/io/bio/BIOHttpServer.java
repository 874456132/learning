package com.learning.bliss.demo.io.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/2/10 17:59
 * @Version 1.0
 */
public class BIOHttpServer {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务器启动成功");
        while (!serverSocket.isClosed()) {
            Socket request = serverSocket.accept();
            System.out.println("收到新连接：" + request.toString());
            // 多线程接收多个连接
            executorService.submit(() -> {
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
                    // 根据HTTP协议组装响应数据包返回数据给浏览器
                    OutputStream outputStream = request.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));

                    /*bufferedWriter.write("HTTP/1.1 200 OK\r\n");
                    bufferedWriter.write("Content-Length: 11\r\n\r\n");
                    bufferedWriter.write("Hello World");*/
                    bufferedWriter.write("HTTP/1.1 200 OK\r\n");
                    bufferedWriter.write("Content-Type: text/html;charset=UTF-8\r\n");
                    bufferedWriter.write("\r\n");
                    bufferedWriter.write("helloWord\r\n");
                    bufferedWriter.flush();
                    bufferedWriter.close();
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

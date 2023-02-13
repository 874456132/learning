package com.learning.bliss.demo.io.bio;

import lombok.SneakyThrows;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/2/10 17:21
 * @Version 1.0
 */
public class BIOClient {
    @SneakyThrows
    public static void main(String[] args) {
        Socket socket = new Socket("localhost", 8080);
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入：");
        String message = scanner.nextLine();
        outputStream.write(message.getBytes(Charset.forName("UTF-8")));
        scanner.close();
        socket.close();
    }
}

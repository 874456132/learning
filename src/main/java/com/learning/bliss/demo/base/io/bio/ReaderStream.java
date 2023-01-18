package com.learning.bliss.demo.base.io.bio;

import java.io.*;
import java.nio.file.FileSystem;
import java.util.concurrent.atomic.LongAdder;

/**
 * 字符流
 *
 * @Author: xuexc
 * @Date: 2021/2/28 17:21
 * @Version 0.1
 */
public class ReaderStream {
    public static void main(String[] args) throws Exception {

        /*******************************************************************/
        /*********************** FileReader ***********************/
        /*******************************************************************/
        /*String fileName = "D:/mysoft/self-study/mysoft/io/hello.txt";
        File f = new File(fileName);*/
        /*char[] ch = new char[100];
        Reader read = new FileReader(f);
        int count = read.read(ch);
        read.close();
        System.out.println("读入的长度为：" + count);
        System.out.println("内容为" + new String(ch, 0, count));*/

        /*Reader inputStreamReader = new InputStreamReader(new FileInputStream(f));
        char[] chars = new char[10];
        StringBuilder stringBuilder = new StringBuilder();
        while (inputStreamReader.read(chars) != -1) {
            stringBuilder = stringBuilder.append(chars);
        }
        inputStreamReader.close();
        System.out.print(stringBuilder.toString());*/

        /*Reader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        char[] chars = new char[10];
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.read(chars) != -1) {
            stringBuilder = stringBuilder.append(chars);
        }
        bufferedReader.close();
        System.out.print(stringBuilder.toString());*/

        /*BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
        StringBuilder stringBuilder = new StringBuilder();
        String str = "";
        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str).append("\n");
        }
        bufferedReader.close();
        System.out.print(stringBuilder.toString());*/

        /*BufferedReader bufferedReader = new BufferedReader(new LineNumberReader(new FileReader(f)));
        StringBuilder stringBuilder = new StringBuilder();
        String str = "";
        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str).append("\n");
        }
        bufferedReader.close();
        System.out.print(stringBuilder.toString());*/


        /*******************************************************************/
        /**************************** FileWriter ***************************/
        /*******************************************************************/
        String fileName = "D:/mysoft/self-study/mysoft/io/hi.txt";
        File file = new File(fileName);
        /*Writer out = new OutputStreamWriter(new FileOutputStream(file));
        out.write("hello\n");
        out.close();*/

        /*Writer out = new FileWriter(file, true);
        out.write("hello");
        out.close();*/

        /*Writer out = new BufferedWriter(new FileWriter(file, true));
        out.write("hello\n");
        out.close();*/

        /*Writer out = new BufferedWriter(new FileWriter(file, true));
        out.write("hello\n");
        out.close();*/

        /*******************************************************************/
        /****************************** 编码问题 ****************************/
        /*******************************************************************/
        System.out.println("系统的默认编码：" + System.getProperty("file.encoding"));
        File f = new File("E:\\mysoft\\heima\\io/hello.txt");
        OutputStream out = new FileOutputStream(f);
        byte[] bytes = "你好".getBytes("ISO8859-1");
        out.write(bytes);
        out.close();
    }
}

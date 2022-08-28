package com.learning.bliss.base.io.bio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * 随机访问流
 *
 * @Author: xuexc
 * @Date: 2021/6/27 16:21
 * @Version 0.1
 */
public class RandomAccessFileDemo {
    public static void main(String[] args) throws Exception {
        /*******************************************************************/
        /************************* RandomAccessFile ************************/
        /*******************************************************************/
        /*RandomAccessFile randomAccessFileR = RAFTestFactory.getRAFWithModelR(fileName);
        System.out.println("length()--获取文本内容长度：" + randomAccessFileR.length());
        System.out.println("getFilePointer()--获取光标位置：" + randomAccessFileR.getFilePointer());
        System.out.println("getFilePointer()--设置光标位置：10");randomAccessFileR.seek(1);
        System.out.println("getFilePointer()--获取光标位置：" + randomAccessFileR.getFilePointer());

        //randomAccessFileR.write("第一次".getBytes());//报异常
        String str = "";
        StringBuilder sb = new StringBuilder();
        while ((str = randomAccessFileR.readLine()) != null) {
            sb = sb.append(str + "\n");
        }
        randomAccessFileR.close();
        System.out.println(sb.toString());*/

        /*String url = "D:/mysoft/self-study/mysoft/io/text.txt";
        RandomAccessFile randomAccessFileRw = RAFTestFactory.getRAFWithModelRW(url);
        System.out.println("length()--获取文本内容长度：" + randomAccessFileRw.length());
        String str = "";
        StringBuilder sb = new StringBuilder();
        while ((str = randomAccessFileRw.readLine()) != null) {
            sb = sb.append(str + "\n");
        }
        System.out.println("文件内容：" + sb.toString());
        int point = (int)randomAccessFileRw.getFilePointer();
        System.out.println("getFilePointer()--获取光标位置：" + point);
        randomAccessFileRw.seek(point);//指定文件的光标位置
        randomAccessFileRw.write("sfsfeww".getBytes(), 0, 6);
        randomAccessFileRw.close();*/

        /*String url = "D:/mysoft/self-study/mysoft/io/text.txt";
        RandomAccessFile randomAccessFileRwd = RAFTestFactory.getRAFWithModelRWD(url);
        System.out.println("length()--获取文本内容长度：" + randomAccessFileRwd.length());
        String str = "";
        StringBuilder sb = new StringBuilder();
        while ((str = randomAccessFileRwd.readLine()) != null) {
            sb = sb.append(str + "\n");
        }
        System.out.println("文件内容：" + sb.toString());
        int point = (int)randomAccessFileRwd.getFilePointer();
        System.out.println("getFilePointer()--获取光标位置：" + point);
        randomAccessFileRwd.seek(point);
        randomAccessFileRwd.write("111222".getBytes(),4,2);
        randomAccessFileRwd.close();*/

        String url = "D:/mysoft/self-study/mysoft/io/text.txt";
        RandomAccessFile randomAccessFileRws = RAFTestFactory.getRAFWithModelRWS(url);
        System.out.println("length()--获取文本内容长度：" + randomAccessFileRws.length());
        String str = "";
        StringBuilder sb = new StringBuilder();
        while ((str = randomAccessFileRws.readLine()) != null) {
            sb = sb.append(str + "\n");
        }
        System.out.println("文件内容：" + sb.toString());
        int point = (int)randomAccessFileRws.getFilePointer();
        System.out.println("getFilePointer()--获取光标位置：" + point);
        randomAccessFileRws.seek(point);
        randomAccessFileRws.write("111222".getBytes(),4,2);
        randomAccessFileRws.close();

    }


    public static class RAFTestFactory {
        private static final String[] model = {"r", "rw", "rws", "rwd"};

        public static RandomAccessFile getRAFWithModelR(String url) throws FileNotFoundException {
            RandomAccessFile raf = new RandomAccessFile(new File(url), model[0]);
            return raf;
        }

        public static RandomAccessFile getRAFWithModelRW(String url) throws FileNotFoundException {
            RandomAccessFile raf = new RandomAccessFile(new File(url), model[1]);
            return raf;
        }

        public static RandomAccessFile getRAFWithModelRWS(String url) throws FileNotFoundException {
            RandomAccessFile raf = new RandomAccessFile(new File(url), model[2]);
            return raf;
        }

        public static RandomAccessFile getRAFWithModelRWD(String url) throws FileNotFoundException {
            RandomAccessFile raf = new RandomAccessFile(new File(url), model[3]);
            return raf;
        }
    }
}

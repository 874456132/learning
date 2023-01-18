package com.learning.bliss.demo.base.io.bio;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 字节流
 *
 * @Author: xuexc
 * @Date: 2021/2/28 17:21
 * @Version 0.1
 */
public class ByteStream {
    public static void main(String[] args) {
        //读取文件内容
        /*File file = new File("io" + File.separator + "a.txt");
        if (!file.getParentFile().isDirectory()) {
            System.out.println("此文件路径有问题");
        }
        if (!file.getParentFile().exists()) {
            System.out.println("此文件/文件路径不存在，主动创建");
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        /*******************************************************************/
        /*************************FileInputStream***************************/
        /*******************************************************************/
        /*try {
            InputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            fileInputStream.read(bytes);
            fileInputStream.close();
            //由于定义的字节长度为1024字节，但是实际并没有填充满，所以输出会有大量空格
            System.out.println(new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //为了避免输出大量的未占满的空格，则可使用read()方法返回的偏移量来定义读取文件内容的真实字节长度
        /*try {
            InputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int ofset = fileInputStream.read(bytes);
            fileInputStream.close();

            System.out.println(new String(bytes, 0, ofset));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //但是虽然避免了输出大量的字节未填满的空格，但是内存浪费问题并没有解决
        //解决办法：预先读取文件的长度，申请字节长度的时候，直接指定和文件长度一致的字节大小
        /*try {
            InputStream fileInputStream = new FileInputStream(file);
            long fileSize = file.length();
            System.out.println("文件长度为：" + fileSize);
            byte[] bytes = new byte[(int) fileSize];
            System.out.println("字节大小为：" + bytes.length);
            fileInputStream.read(bytes);
            fileInputStream.close();

            System.out.println(new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //逐字节读文件
        /*try {
            InputStream fileInputStream = new FileInputStream(file);
            long fileSize = file.length();
            System.out.println("文件长度为：" + fileSize);
            byte[] bytes = new byte[(int) fileSize];
            System.out.println("字节大小为：" + bytes.length);
            for(int i = 0; i < bytes.length; i++){
                bytes[i] = (byte) fileInputStream.read();
            }
            fileInputStream.close();

            System.out.println(new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //以上几种方法都是建立在知晓文件长度的基础上的，在不清楚文件长度的基础上，则需要通过判断是否读取至文件的末尾
        /*try {
            InputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int temp;
            for(int i = 0; (temp = fileInputStream.read()) != -1; i++){
                bytes[i] = (byte) temp;
            }
            fileInputStream.close();
            System.out.println(new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*******************************************************************/
        /*************************DataInputStream***************************/
        /*******************************************************************/
        //写数据：以数据类型对应的特定的写入方法将数据以特定格式写入文件
        //读数据：将文件中的数据以特定的读入方法读取对应数据类型的值
        /*try {
            File file1 = new File("io" + File.separator + "b.txt");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file1));
            dos.writeUTF("我");
            dos.writeInt(23111);
            dos.writeBoolean(true);
            dos.writeShort((short) 123);
            dos.writeLong((long) 456);
            dos.writeDouble(99.98);
            dos.writeInt(99282);
            DataInputStream dis = new DataInputStream(new FileInputStream(file1));
            System.out.println(dis.readUTF());
            System.out.println(dis.readInt());
            System.out.println(dis.readBoolean());
            System.out.println(dis.readShort());
            System.out.println(dis.readLong());
            System.out.println(dis.readDouble());
            System.out.println(dis.readInt());
            dis.close();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*******************************************************************/
        /*************************PushbackInputStream***********************/
        /*******************************************************************/
        //回退流其实是维护了一个数组，将回退的字符放入此数组中
        /*try {
            String str = "hello,rollenholt";
            PushbackInputStream push;
            ByteArrayInputStream bat;
            bat = new ByteArrayInputStream(str.getBytes());
            push = new PushbackInputStream(bat);
            int temp = 0;
            while ((temp = bat.read()) != -1) {
                if (temp == ',') {
                    push.unread(temp);
                    temp = bat.read();
                    System.out.print("(回退" + (char) temp + ") ");
                } else {
                    System.out.print((char) temp);
                }
            }
            System.out.print("\n**************************\n");
            System.out.print(new String(push.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*******************************************************************/
        /**********************字节输出流 outputStream***********************/
        /*******************************************************************/
        //写文件时不用创建文件目录以及空文件。

        File outFile = new File("io" + File.separator + "outFile.txt");

        //向文件中写入字符串
        /*OutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            String str = "天行健，君子以自强不息";
            byte[] b = str.getBytes();
            out.write(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
        //逐字节写入文件
        /*OutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            String str = "地势坤，君子以厚德载物";
            byte[] b = str.getBytes();
            int i = 0;
            while (i < b.length){
                out.write(b[i++]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
        //向已有文件中追加新内容
        /*OutputStream out = null;
        try {
            out = new FileOutputStream(outFile, true);
            String str = "地势坤，君子以厚德载物";
            byte[] b = str.getBytes();
            int i = 0;
            while (i < b.length){
                out.write(b[i++]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
        //复制文件（先将源文件内容读取，然后再写入新文件中）
        /*File sourceFile = new File("io" + File.separator + "a.txt");
        if (!sourceFile.getParentFile().isDirectory()) {
            System.out.println("此文件路径有问题");
        }
        if (!sourceFile.getParentFile().exists()) {
            System.out.println("此文件/文件路径不存在，主动创建");
            sourceFile.getParentFile().mkdirs();
        }
        if (!sourceFile.exists()) {
            try {
                sourceFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream in = null;
        OutputStream out = null;
        try{
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(outFile);
            if(in != null && out != null){
                int temp = 0;
                while ((temp = in.read()) != -1){
                    out.write(temp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }*/
        //使用内存操作流将一个大写字母转化为小写字母
        /*String sourceStr = "ABCDEF";
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new ByteArrayInputStream(sourceStr.getBytes());
            out = new ByteArrayOutputStream();
            int temp = 0;
            while ((temp = in.read()) != -1){
                char c = (char)temp;
                out.write(Character.toLowerCase(c));
            }
            System.out.println(out.toString());
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }*/

        /** 验证管道流（PipedInputStream、PipedOutputStream）：进程间通信 */
        /*Send send = new Send();
        Recive recive = new Recive();
        try {
            //管道连接
            send.getOut().connect(recive.getInput());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(send).start();
        new Thread(recive).start();*/

        //DataOutputStream
        /*if (!outFile.getParentFile().isDirectory()) {
            System.out.println("此文件路径有问题");
        }
        if (!outFile.getParentFile().exists()) {
            System.out.println("此文件/文件路径不存在，主动创建");
            outFile.getParentFile().mkdirs();
        }
        if (!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(new FileOutputStream(outFile));//此文件必须存在，否则抛FileNotFoundException异常
            char[] c = {'A','B','C','D','E','F',};
            for(char cc : c){
                //dataOutputStream.writeChar(cc); //writeChar：写文件时，每次字符前加个空格
                dataOutputStream.write(cc);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }*/

        /*******************************************************************/
        /********************* 压缩输出流 ZipOutputStream*********************/
        /*******************************************************************/
        //单个文件压缩
        /**
         * 步骤：①构建待压缩文件的输出流new ZipOutputStream(new FileOutputStream(zipFile))
         *      ②构建读取待压缩文件的输入流new FileInputStream(outFile)
         *      ③设置压缩文件的条目zipOutputStream.putNextEntry(new ZipEntry(outFile.getName()));
         *      ④设置注释zipOutputStream.setComment("初次学习")
         *      ⑤读取文件中内容并写入输出流zipOutputStream
         */
        /*File zipFile = new File("io" + File.separator + "outFile.zip");
        InputStream inputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
            inputStream = new FileInputStream(outFile);
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOutputStream.putNextEntry(new ZipEntry(outFile.getName()));//ZipEntry  表示压缩文件的条目(就相当与java文件中的directory目录一样)
            zipOutputStream.setComment("初次学习");//设置注释
            //读取outFile文件中的内容，并写入zipFile中
            int temp = 0;
            while((temp = inputStream.read()) != -1){
                zipOutputStream.write(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if(zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }*/
        //多个文件压缩
        /*File zipFile = new File("io" + File.separator + "outFile.zip");
        File fileDir = new File("src\\main\\java\\com\\hell\\study\\base\\io\\bio");
        InputStream inputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File file : fileDir.listFiles()) {
                if (file.isFile()) {
                    inputStream = new FileInputStream(file);
                    zipOutputStream.putNextEntry(new ZipEntry(file.getName()));//ZipEntry  表示压缩文件的条目(就相当与java文件中的directory目录一样)
                    byte[] b = new byte[1024];
                    while (inputStream.read(b) != -1) {
                        zipOutputStream.write(b);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }*/
        /** 解压 */
        /**
         * 步骤：①创建解压文件目录；②通过压缩文件路径构建ZipFile对象new ZipFile(new File(path))，通过包装流ZipInputStream包装一个FileInputStream(file)；
         * ③循环遍历取解压文件中的条目（逐个文件），zipInput。getNextEntry()；④逐个创建当前解压文件的对应相同文件名的空文件；⑤调用ZipFile对象的getInputStream(zipEntry)压缩文件中当前条目的流信息
         * ⑥读取当前流信息中的内容（zipInput）,并将读取到的内容写入输出流中。
         */
        /*File file = new File("io" + File.separator + "outFile.zip");
        try {
            ZipFile zipFile = new ZipFile(file);
            ZipInputStream zipInput = new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            String unZipFileDirPath = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(":") + 1, file.getAbsolutePath().lastIndexOf("."));
            File unZipFileDir = new File(unZipFileDirPath);
            if(!unZipFileDir.exists()){
                unZipFileDir.mkdirs();
            }
            while ((zipEntry = zipInput.getNextEntry()) != null){
                try {
                    System.out.println("解压文件：" + zipEntry.getName());
                    File unZipFile = new File(unZipFileDirPath + File.separator + zipEntry.getName());
                    if(!unZipFile.exists()){
                        unZipFile.createNewFile();
                    }
                    inputStream = zipFile.getInputStream(zipEntry);
                    outputStream = new FileOutputStream(unZipFile);
                    int temp = 0;
                    while((temp = inputStream.read()) != -1){
                        outputStream.write(temp);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (zipInput != null) {
                        try {
                            zipInput.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /** 多文件压缩解压 */
        /*ZipManager manager = new ZipManager();
        *//*try {
            manager.createZip("D:\\mysoft\\self-study\\mysoft\\target", "D:\\mysoft\\self-study\\mysoft\\io\\target.zip");
        } catch (Exception e) {
            System.out.println("error");
        }*//*
        try {
            manager.releaseZipToFile("D:\\mysoft\\self-study\\mysoft\\io\\target.zip", "D:\\mysoft\\self-study\\mysoft\\io\\target");
        } catch (Exception e) {
            System.out.println("error");
        }
        System.out.println("over");*/

        /*******************************************************************/
        /******************* 合并流 SequenceInputStream *********************/
        /*******************************************************************/
        /*File file1 = new File("D:\\mysoft\\self-study\\mysoft\\io\\first.txt");
        File file2 = new File("D:\\mysoft\\self-study\\mysoft\\io\\second.txt");
        File file3 = new File("D:\\mysoft\\self-study\\mysoft\\io" + File.separator + "hello.txt");
        InputStream input1 = null;
        InputStream input2 = null;
        OutputStream output = null;
        SequenceInputStream sis = null;
        try {
            input1 = new FileInputStream(file1);
            input2 = new FileInputStream(file2);
            output = new FileOutputStream(file3);
            // 合并流
            sis = new SequenceInputStream(input1, input2);
            byte[] b = new byte[1024];
            while(sis.read(b) != -1){
                output.write(b);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(input1 != null){
                try {
                    input1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(input2 != null){
                try {
                    input2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(output != null){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(sis != null){
                try {
                    sis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
        /*******************************************************************/
        /************************ 工具类 PrintStream ************************/
        /*************** 本质上是对其它流的综合运用的一个工具而已******************/
        /*try {
            PrintStream ps = new PrintStream(new FileOutputStream("D:\\mysoft\\self-study\\mysoft\\io\\first1.txt"));
            String name = "张三";
            String age = "18";
            ps.print(name);
            ps.println(age);
            ps.printf("姓名：%s，年龄：%s。", name, age);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        /*******************************************************************/
        /****************** 使用OutputStream向控制台上输出内容 *****************/
        /*******************************************************************/
        /*OutputStream out = System.out;
        try{
            out.write("hello".getBytes());
        }catch (Exception e) {
            e.printStackTrace();
        }
        try{
            out.close();
        }catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    /**
     * 消息发送类
     */
    public static class Send implements Runnable {
        private PipedOutputStream out = null;

        public Send() {
            out = new PipedOutputStream();
        }

        public PipedOutputStream getOut() {
            return this.out;
        }

        public void run() {
            String message = "hello , Rollen";
            try {
                out.write(message.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 接受消息类
     */
    public static class Recive implements Runnable {
        private PipedInputStream input = null;

        public Recive() {
            this.input = new PipedInputStream();
        }

        public PipedInputStream getInput() {
            return this.input;
        }

        public void run() {
            byte[] b = new byte[1024];
            int len = 0;
            try {
                len = this.input.read(b);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("接受的内容为 " + (new String(b, 0, len)));
        }
    }
}

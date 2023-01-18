package com.learning.bliss.demo.base.io.bio;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
  * File类
  * @Author: xuexc
  * @Date: 2021/2/24 20:04
  * @Version 0.1
  */
class FileUtils {

    public static void main(String[] args) {
        System.out.println(File.separator);//输出 \
        System.out.println(File.pathSeparator);//输出 ;\
        //File类的构造方法
        File file1 = new File("E:\\mysoft\\heima\\io" + File.separator + "aa");
        File file2 = new File(file1,"io2" + File.separator + "a.txt");
        File file3 = new File("io","io3"+ File.separator + "a.txt");
        File file4 = new File(file1,"io2" + File.separator + "b.txt");


        System.out.println(System.getProperty("user.dir"));
        /*try {
            boolean a = file1.createNewFile();//路径不存在返回false
            System.out.println(file1.getPath());
            System.out.println(a);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //创建
        //创建子目录 mkdir()
        /*System.out.println(file1.mkdir());
        System.out.println(file1.getPath());*/
        //创建多层目录 mkdirs()
        /*System.out.println(file1.mkdirs());
        System.out.println(file1.getPath());*/

        //删除
        //删除文件或目录 delete()  删除文件或目录（只删除子目录），如果表示目录，则目录下必须为空，否则返回false。
        //System.out.println(file1.delete());
        //文件使用完成后删除
        file1.deleteOnExit();
        System.out.println("=========getName()==============");
        System.out.println(file1.getName());
        System.out.println(file2.getName());
        System.out.println("=========getPath()==============");
        System.out.println(file1.getPath());
        System.out.println(file2.getPath());
        System.out.println("=========getAbsolutePath()==============");
        System.out.println(file1.getAbsolutePath());
        System.out.println(file2.getAbsolutePath());
        System.out.println("=========getParent()==============");
        System.out.println(file1.getParent());
        System.out.println(file2.getParent());
        System.out.println("=========lastModified()==============");
        System.out.println(file1.lastModified());
        System.out.println(file2.lastModified());
        System.out.println("=========length()==============");
        System.out.println(file1.length());
        System.out.println(file2.length());
        System.out.println("=========renameTo()==============");
        System.out.println(file2.renameTo(file3));
        System.out.println("=========isAbsolute()==============");
        System.out.println(file2.isAbsolute());
        System.out.println("=========listFiles()==============");
        File[] files = file1.getParentFile().getParentFile().listFiles();
        for(File f : files){
            System.out.println(f.getPath());
        }
        printLoopFilePath(file1.getParentFile().getParentFile());
    }
    //打印给定目录下的所有文件夹和文件夹里面的内容
    public static void printLoopFilePath(File file){
        File[] files = file.listFiles();
        for(File f : files){
            System.out.println(f.getPath());
            if(f.isDirectory()){
                printLoopFilePath(f);
            }
        }
    }
}

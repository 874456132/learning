package com.learning.bliss.niuke;

import java.util.Scanner;

/**
 * 响应式编程
 * ReactiveRedisTemplate 响应式操作类，用来以响应式编程的方式去操作 Redis
 *
 * @Author xuexc
 * @Date 2022/12/30 18:40
 * @Version 1.0
 */

public class StrTest {


    public static void main(String[] args) {

        //计算某字符出现次数
        //statisticsChar();

        //字符串最后一个单词的长度
        //lastWordLength();

        //明明的随机数 数组去重并排序
        //arraysDeduplicatedSorted();

        //字符串分隔
        //splitString();

        //进制转换
        //baseConversion();

        //质数因子
        primeFactor();
    }

    //计算某字符出现次数
    private static void statisticsChar() {
        Scanner in = new Scanner(System.in);
        String inputStr = in.nextLine();
        String inputChar = in.nextLine();
        System.out.println(inputStr.length() - inputStr.replaceAll(inputChar,
                "").length());
    }

    //字符串最后一个单词的长度
    private static void lastWordLength() {
        Scanner scanner = new Scanner(System.in);

        String str = scanner.nextLine();
        int size = str.length();
        int len = 0;
        while (size-- > 0 && str.charAt(size) != 32) {
            len++;
        }
        System.out.println(len);
    }

    //字符串分隔
    private static void splitString() {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        StringBuffer stringBuffer = new StringBuffer(s);
        int remainder = s.length() % 8;
        if (remainder != 0) {
            int apendNum = 8 - remainder;
            while (apendNum > 0) {
                stringBuffer.append('0');
                apendNum--;
            }
        }
        int length = stringBuffer.length();
        for (int left = 0; left < length; left = left + 8) {
            System.out.println(stringBuffer.substring(left, left + 8));
        }
    }

    //进制转换
    public static void baseConversion() {

        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String number = in.nextLine().substring(2);
            //A：65  a：97
            int num = 0;
            int length = number.length();
            for(int i = 0; i < length; i++) {
                char c = number.charAt(i);
                if(65 <= c && c < 70) {//大写字母
                    num =  num + (c - 55) * (int)Math.pow(16,length - i - 1);
                } else if(97 <= c && c < 102) {
                    num =  num + (c - 55 - 32) * (int)Math.pow(16,length - i - 1);
                } else
                    num =  num + Character.getNumericValue(c) * (int)Math.pow(16,length - i - 1);
            }

            System.out.println(num);
        }
    }

    //质数因子
    public static void primeFactor() {
        // 处理输入
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            // 获取需要求解的值
            int target = sc.nextInt();
            int y = 2;// 因子从2开始算
            while(target != 1){ // 短除法，除到目标值为1为止
                if(target % y == 0) // 能能够整除2
                {
                    System.out.print(y+" ");
                    target /= y;
                }else{// 更新y的值
                    if(y > target / y) y = target;//如果剩余值为质数
                    else y++;  //y值增加1
                }
            }
        }
    }

}

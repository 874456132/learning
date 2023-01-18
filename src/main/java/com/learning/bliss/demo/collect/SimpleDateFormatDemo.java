package com.learning.bliss.demo.collect;

import lombok.SneakyThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2021/7/19 14:37
 * @Version 1.0
 */
public class SimpleDateFormatDemo {
    private static String[] strings = {"2021-01-01", "2021-02-01", "2021-03-01", "2021-04-01", "2021-05-01", "2021-06-01", "2021-07-01", "2021-08-01"};
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        for (int i = 0; i < 20; i++) {
            RunnableImpl t = new RunnableImpl();
            executor.submit(t);
        }
        while (executor.getActiveCount() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdownNow();
    }

    public static class RunnableImpl implements Runnable {

        @Override
        public void run() {
            /**
             * SimpleDateFormat.parse()方法禁用，存在线程安全问题：
             * java.text.SimpleDateFormat.parse()，内部构建Calendar对象时调用java.text.CalendarBuilder.establish()，而establish方法内部会先调用Calendar.clear()，
             * 然后调用Calendar.set()，如果一个线程先调用了set()然后另一个线程又调用了clear()，这时候parse()方法解析的时间就不对了
             */
            Date dateParse = null;
            try {
                dateParse = sdf.parse("2021-07-19");
                if (!new SimpleDateFormat("yyyy-MM-dd").format(dateParse).equals("2021-07-19")) {
                    System.out.println("parse()解析前的数据：2021-07-19,解析后的数据：" + new SimpleDateFormat("yyyy-MM-dd").format(dateParse));
                }
                /**
                 *  SimpleDateFormat.format()方法禁用，存在线程安全问题：
                 * java.text.SimpleDateFormat.format()，第一步先设置calendar.setTime(date)
                 */
                int point = (int) (Math.random() * 100) % 8;
                Date dateFormat = new SimpleDateFormat("yyyy-MM-dd").parse(strings[point]);
                String d = sdf.format(dateFormat);
                if (!strings[point].equals(d)) {
                    System.out.println("format()格式化前数据：" + strings[point] + ",格式化后数据：" + d);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}

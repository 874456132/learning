package com.learning.bliss.demo.collect;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2021/7/19 14:37
 * @Version 1.0
 */
public class DecimalFormatDemo {
    private static BigDecimal[] decimals = {new BigDecimal("1.3283"), new BigDecimal("1.90323"), new BigDecimal("1.85323"),
            new BigDecimal("1.902023"), new BigDecimal("1.324223"), new BigDecimal("1.324123"), new BigDecimal("1.34387523"), new BigDecimal("1.111123")};
    private static final DecimalFormat sdf = new DecimalFormat("0.000");

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1000);
        for (int i = 0; i < 1000; i++) {
            DecimalFormatDemo.RunnableImpl t = new DecimalFormatDemo.RunnableImpl();
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
            int point = (int) (Math.random() * 100) % 8;
            BigDecimal bigDecimalParse = null;
            try {
                bigDecimalParse = (BigDecimal) sdf.parse(decimals[point].toPlainString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (decimals[point].compareTo(bigDecimalParse) != 0) {
                System.out.println("parse()解析前的数据：" + decimals[point].toPlainString() + ",解析后的数据：" + new DecimalFormat("0.000").format(bigDecimalParse));
            }
            /**
             * SimpleDateFormat.format()方法禁用，存在线程安全问题：
             * java.text.SimpleDateFormat.format()，第一步先设置calendar.setTime(date)
             */
            String d = sdf.format(decimals[point]);
            if (!decimals[point].toPlainString().equals(d)) {
                System.out.println("format()格式化前数据：" + decimals[point].toPlainString() + ",格式化后数据：" + d);
            }
        }
    }
}

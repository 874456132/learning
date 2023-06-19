package com.learning.bliss;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/3/21 11:29
 * @Version 1.0
 */
public class IncreKey {
    private byte byteCache = 10;
    private byte flag = 1;
    private int terminalNo = 1;

    /*public static Long getIncreKey(){
        return owner.compareAndSet(null, Thread.currentThread());
    }*/

    public static void main(String[] args) {
        System.out.println( 16 & 8 ^ 8);

        Integer integer = new Integer(10);
        System.out.println(integer == 10);
        System.out.println(integer == new Integer(10));

        String s = "a";
        System.out.println(s == "a");

        //String a = new String("a");
        String a = "a";
        System.out.println(a == "a");
    }

}

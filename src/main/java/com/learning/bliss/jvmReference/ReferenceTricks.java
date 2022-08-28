package com.learning.bliss.jvmReference;

/**
  * Reference学习，属于jvm通过句柄访问对象
  * @Author: xuexc
  * @Date: 2020/12/12 17:32
  * @Version 0.1
  */
public class ReferenceTricks {
    public int i;

    public static void main(String[] args) {
        ReferenceTricks r = new ReferenceTricks();
        // reset integer
        r.i = 0;
        System.out.println("Before changeInteger:" + r.i);
        changeInteger(r);
        System.out.println("After changeInteger:" + r.i);

        // just for format
        System.out.println();

        // reset integer
        r.i = 0;
        System.out.println("Before changeReference:" + r.i);
        changeReference(r);
        System.out.println("After changeReference:" + r.i);
    }

    private static void changeReference(ReferenceTricks r) {
        System.out.println("hashCodeAddr r: " + r);
        r = new ReferenceTricks();
        System.out.println("hashCodeAddr new r: " + r);
        r.i = 5;
        System.out.println("In changeReference: " + r.i);
    }

    private static void changeInteger(ReferenceTricks r) {
        r.i = 5;
        System.out.println("In changeInteger:" + r.i);
    }


}

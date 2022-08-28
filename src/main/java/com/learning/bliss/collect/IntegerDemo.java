package com.learning.bliss.collect;

import java.lang.reflect.Field;

/**
 * 黄小斜学Java公众号
 *
 * @Author xuexc
 * @Date 2021/6/10 18:30
 * @Version 1.0
 */
public class IntegerDemo {

    /*//默认情况下-128到127(包含)的数字做缓存以供自动装箱使用，所以在值为-128到127之间的Integer对象其实直接引用的是缓存中的值，所以相等
    public static void main(String[] args) {
        Integer a = -128, b = -128;
        Integer c = 100, d = 100;
        Integer e = 127, f = 127;
        System.out.println(a == b);
        System.out.println(c == d);
        System.out.println(e == f);
    }*/
    public static void main(String[] args) {
        /**
         * Integer内部维护着一个 IntegerCache（通过Integer[]实现） 的缓存，默认缓存范围是 [-128, 127]，所以 [-128, 127] 之间的值用 == 和 != 比较的其实是真实的值，这也是自动装箱和拆箱的原理
         * 以下示例中其实是修改了IntegerCache中对象2（array[130]），对象3（array[131]）对应的值，从而使他们等于对象1的值
         */
        Class cache = Integer.class.getDeclaredClasses()[0];
        Field c = null;
        try {
            c = cache.getDeclaredField("cache");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        c.setAccessible(true);
        Integer[] array = new Integer[0];
        try {
            array = (Integer[]) c.get(cache);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // array[129] is 1
        array[130] = array[129];
        // Set 2 to be 1
        array[131] = array[129];
        // Set 3 to be 1
        Integer a = 1;
        if (a == (Integer) 1 && a == (Integer) 2 && a == (Integer) 3) {
            System.out.println("Success");
        }
    }

}

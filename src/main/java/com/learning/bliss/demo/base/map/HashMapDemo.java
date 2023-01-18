package com.learning.bliss.demo.base.map;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2021/12/16 11:12
 * @Version 1.0
 */
public class HashMapDemo {
    public static void main(String[] args) {
        HashMap map = new HashMap();
        for(int i = 0; i < 12; i++){
            map.put((i + 1), (i + 1) + "");
        }
        try {
            Field table = HashMap.class.getDeclaredField("table");
            table.setAccessible(true);
            Object[] objects = (Object[])table.get(map);
            System.out.println(objects.length);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        TreeMap treeMap = new TreeMap();
        treeMap.put("weq", "4412");
        treeMap.put("dawdq", "53");
        treeMap.put("qwdqa", "45");
        treeMap.put("weq", "78");
        treeMap.forEach((key, value) -> {
            System.out.println("key :" + key + "-> value :" + value);
        });
    }

}

package com.learning.bliss.demo.cache;

import lombok.Data;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/16 18:48
 * @Version 1.0
 */
@Data
public class CaffeineCacheBean implements ICaffeineCacheBean{

    private String id;

    private String name;

    private int age;

    @Override
    public String getId() {
        return id;
    }
}

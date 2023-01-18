package com.learning.bliss.demo.cache;

import lombok.Data;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;


@Service
public class RedisCacheDemo {
    @Cacheable(cacheNames = "USER", key="#userId")
    public User queryUser(String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setName("张三");
        user.setAge("18");
        System.out.println("查询数据库user=" + user.toString());
        return user;
    }

    @Data
    public static class User implements Serializable {

        private static final long serialVersionUID = 1L;

        private String userId;
        private String name;
        private String age;
    }

}

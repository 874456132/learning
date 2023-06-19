package com.learning.bliss.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.learning.bliss.demo.cache.CaffeineCacheBean;
import com.learning.bliss.demo.cache.CaffeineCacheHandle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author xuexc
 * @Date 2021/12/8 14:37
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CaffeineCacheTest {

    @Autowired
    private CaffeineCacheHandle caffeineCacheHandle;
    @Autowired
    private Caffeine<Object, Object> caffeineCacheConfig;
    @Autowired
    private CaffeineCacheManager caffeineCacheManager;

    @Test
    public void multBeanTest() {
        System.out.println("caffeineCacheConfig   " + caffeineCacheConfig.toString());

        System.out.println("caffeineCacheManager    " + caffeineCacheManager.getClass().toString());

    }

    @Test
    public void cacheTest() throws InterruptedException {
        CaffeineCacheBean caffeineCacheBean = new CaffeineCacheBean();
        caffeineCacheBean.setId("00001");
        caffeineCacheBean.setName("张三");
        caffeineCacheBean.setAge(23);
        caffeineCacheHandle.addUserInfo(caffeineCacheBean);
        Thread.sleep(3000);
        new Thread(() -> {
            System.out.println(caffeineCacheHandle.getByName("00001"));
        }).start();

        Thread.sleep(1000);
        caffeineCacheBean.setName("李四");
        caffeineCacheHandle.updateUserInfo(caffeineCacheBean);

        Thread.sleep(3000);
        new Thread(() -> {
            System.out.println(caffeineCacheHandle.getByName("00001"));
        }).start();
    }

}


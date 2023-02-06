package com.learning.bliss.cache;

import com.learning.bliss.demo.cache.RedisCacheDemo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/30 11:51
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("standalone")
public class RedisCacheTest {

    @Autowired
    private RedisCacheDemo redisCacheDemo;

    @Test
    public void test() {

    }
}

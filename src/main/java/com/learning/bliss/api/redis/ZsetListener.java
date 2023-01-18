package com.learning.bliss.api.redis;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/6 12:32
 * @Version 1.0
 */
public interface ZsetListener {
    void onMessage(Object obj);
}

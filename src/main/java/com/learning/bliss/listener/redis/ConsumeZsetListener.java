package com.learning.bliss.listener.redis;

import com.alibaba.fastjson.JSONObject;
import com.learning.bliss.annotation.redis.AsyncConsumeZset;
import com.learning.bliss.api.redis.ListsListener;
import com.learning.bliss.constant.RedisConstant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2023/1/6 12:29
 * @Version 1.0
 */
public class ConsumeZsetListener implements ListsListener {
    /**
     * 统一消息发送
     *
     * @param obj 微信消息模版
     */
    @AsyncConsumeZset(queue = RedisConstant.QUEUE_MSG)
    @Override
    public void onMessage(Object obj) {
        System.err.println("prop:" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        System.err.println(JSONObject.toJSONString(obj));
    }

}

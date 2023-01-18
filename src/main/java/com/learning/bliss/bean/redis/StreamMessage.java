package com.learning.bliss.bean.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Stream队列消息实体
 *
 * @Author xuexc
 * @Date 2023/1/4 23:19
 * @Version 1.0
 */

@Getter
@Setter
@ToString
public class StreamMessage {
    private String title;
    private String author;
    private Object content;

    public static StreamMessage create(String title, String author, Object content) {
        StreamMessage message = new StreamMessage();
        message.setTitle(title);
        message.setAuthor(author);
        message.setContent(content);
        return message;
    }
}

package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.simplequeue.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: 消息生产者
 * @Author: weixiaohuai
 * @Date: 2019/6/22
 * @Time: 21:37
 */
public class CustomProducer {

    private static final String SIMPLE_QUEUE_NAME = "MQ_SIMPLE_QUEUE";
    private static final String SIMPLE_QUEUE_MESSAGE = "CUSTOMPRODUCER --> SIMPLE_QUEUE_NAME2";

    public static void main(String[] args) {
        //获取MQ连接
        Connection connection = MQConnecitonUtils.getConnection();
        //从连接中获取Channel通道对象
        Channel channel = null;
        try {
            channel = connection.createChannel();
            //创建Queue队列
            channel.queueDeclare(SIMPLE_QUEUE_NAME, false, false, false, null);
            //发送消息
            channel.basicPublish("", SIMPLE_QUEUE_NAME, null, SIMPLE_QUEUE_MESSAGE.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != channel) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if (null != connection) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

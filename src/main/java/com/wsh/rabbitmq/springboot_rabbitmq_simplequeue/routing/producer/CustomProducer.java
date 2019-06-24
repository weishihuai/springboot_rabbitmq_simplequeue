package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.routing.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: routing路由模式
 * @Author: weixiaohuai
 * @Date: 2019/6/23
 * @Time: 15:20
 * <p>
 * 说明：生产者发送消息的时候指定routing key,然后消费者绑定队列的时候也指定一些binding key，只有binding key与routing key一致的消费者才能接收到此消息
 */
public class CustomProducer {
    private static final String EXCHANGE_NAME = "publish_subscribe_exchange_direct";
    //交换机类型：direct
    private static final String EXCHANGE_TYPE = "direct";
    private static final String EXCHANGE_ROUTE_KEY = "info";

    public static void main(String[] args) {
        //获取MQ连接
        Connection connection = MQConnecitonUtils.getConnection();
        //从连接中获取Channel通道对象
        Channel channel = null;
        try {
            channel = connection.createChannel();
            //创建交换机对象
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
            //发送消息到交换机exchange上
            String msg = "hello world!!!";
            //指定routing key为info
            channel.basicPublish(EXCHANGE_NAME, EXCHANGE_ROUTE_KEY, null, msg.getBytes());
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

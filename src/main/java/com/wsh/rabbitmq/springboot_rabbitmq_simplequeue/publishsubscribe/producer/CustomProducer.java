package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.publishsubscribe.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: 发布-订阅模式
 * @Author: weixiaohuai
 * @Date: 2019/6/23
 * @Time: 15:20
 * <p>
 * 说明：可实现一条消息被多个消费者消费
 * <p>
 * a. 一个生产者，多个消费者；
 * b. 每一个消费者都有自己的消息队列；
 * c.生产者没有把消息发送到队列，而是发送到交换器exchange上；
 * d.每个队列都需要绑定到交换机上；
 * e.生产者生产的消息 经过交换机 到达队列，一个消息可以被多个消费者消费；
 *
 */
public class CustomProducer {
    private static final String PUBLISH_SUBSCRIBE_EXCHANGE_NAME = "publish_subscribe_exchange_fanout";
    //类型：分发
    private static final String PUBLISH_SUBSCRIBE_EXCHANGE_TYPE = "fanout";

    public static void main(String[] args) {
        //获取MQ连接
        Connection connection = MQConnecitonUtils.getConnection();
        //从连接中获取Channel通道对象
        Channel channel = null;
        try {
            channel = connection.createChannel();
            //创建交换机对象
            channel.exchangeDeclare(PUBLISH_SUBSCRIBE_EXCHANGE_NAME, PUBLISH_SUBSCRIBE_EXCHANGE_TYPE);
            //发送消息到交换机exchange上
            String msg = "publish_subscribe_exchange_fanout";
            channel.basicPublish(PUBLISH_SUBSCRIBE_EXCHANGE_NAME, "", null, msg.getBytes());
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

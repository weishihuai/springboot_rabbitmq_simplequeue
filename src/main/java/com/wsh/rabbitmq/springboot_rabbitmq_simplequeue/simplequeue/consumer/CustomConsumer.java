package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.simplequeue.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: 消息消费者
 * @Author: weixiaohuai
 * @Date: 2019/6/22
 * @Time: 21:55
 */
public class CustomConsumer {
    private static Logger logger = LoggerFactory.getLogger(CustomConsumer.class);

    private static final String SIMPLE_QUEUE_NAME = "MQ_SIMPLE_QUEUE";

    public static void main(String[] args) {
        //获取MQ连接对象
        Connection connection = MQConnecitonUtils.getConnection();
        Channel channel = null;
        try {
            //创建消息通道对象
            channel = connection.createChannel();
            //创建消费者对象
            QueueingConsumer consumer = new QueueingConsumer(channel);
            //监听消息队列
            channel.basicConsume(SIMPLE_QUEUE_NAME, true, consumer);
            while (true) {
                try {
                    //消息消费者获取消息
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    String message = new String(delivery.getBody());
                    logger.info("receive message: " + message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

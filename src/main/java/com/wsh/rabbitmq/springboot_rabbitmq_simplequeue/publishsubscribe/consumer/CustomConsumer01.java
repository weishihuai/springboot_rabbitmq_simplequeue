package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.publishsubscribe.consumer;

import com.rabbitmq.client.*;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.simplequeue.consumer.CustomConsumer;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Description:
 * @Author: weixiaohuai
 * @Date: 2019/6/23
 * @Time: 15:28
 */
public class CustomConsumer01 {
    private static Logger logger = LoggerFactory.getLogger(CustomConsumer.class);
    private static final String PUBLIC_SUBSCRIBE_QUEUE_NAME = "public_subscribe_queue_name01";
    private static final String PUBLISH_SUBSCRIBE_EXCHANGE_NAME = "publish_subscribe_exchange_fanout";

    public static void main(String[] args) {
        //获取MQ连接对象
        Connection connection = MQConnecitonUtils.getConnection();
        try {
            //创建消息通道对象
            final Channel channel = connection.createChannel();
            //创建队列
            channel.queueDeclare(PUBLIC_SUBSCRIBE_QUEUE_NAME, false, false, false, null);
            //将队列绑定到交换机上
            channel.queueBind(PUBLIC_SUBSCRIBE_QUEUE_NAME, PUBLISH_SUBSCRIBE_EXCHANGE_NAME, "");

            channel.basicQos(1);

            //创建消费者对象
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //消息消费者获取消息
                    String message = new String(body, StandardCharsets.UTF_8);
                    logger.info("【CustomConsumer01】receive message: " + message);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        //消费完一条消息需要自动发送确认消息给MQ
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            };

            //使用公平分发必须关闭自动应答
            boolean autoAck = false;
            //监听消息队列
            channel.basicConsume(PUBLIC_SUBSCRIBE_QUEUE_NAME, autoAck, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

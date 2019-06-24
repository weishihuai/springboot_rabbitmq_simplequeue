package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.routing.consumer;

import com.rabbitmq.client.*;
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
public class CustomConsumer02 {
    private static Logger logger = LoggerFactory.getLogger(CustomConsumer02.class);
    private static final String QUEUE_NAME = "routing_direct_queue_name02";
    private static final String EXCHANGE_NAME = "publish_subscribe_exchange_direct";
    private static final String EXCHANGE_ROUTE_KEY01 = "error";
    private static final String EXCHANGE_ROUTE_KEY02 = "info";
    private static final String EXCHANGE_ROUTE_KEY03 = "warning";

    public static void main(String[] args) {
        //获取MQ连接对象
        Connection connection = MQConnecitonUtils.getConnection();
        try {
            //创建消息通道对象
            final Channel channel = connection.createChannel();
            //创建队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //将队列绑定到交换机上,并且指定routing_key
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, EXCHANGE_ROUTE_KEY01);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, EXCHANGE_ROUTE_KEY02);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, EXCHANGE_ROUTE_KEY03);

            channel.basicQos(1);

            //创建消费者对象
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //消息消费者获取消息
                    String message = new String(body, StandardCharsets.UTF_8);
                    logger.info("【CustomConsumer02】receive message: " + message);
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
            channel.basicConsume(QUEUE_NAME, autoAck, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

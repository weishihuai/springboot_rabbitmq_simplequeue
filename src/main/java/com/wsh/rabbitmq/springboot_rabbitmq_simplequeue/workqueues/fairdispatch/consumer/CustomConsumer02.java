package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.workqueues.fairdispatch.consumer;

import com.rabbitmq.client.*;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomConsumer02 {
    private static Logger logger = LoggerFactory.getLogger(CustomConsumer02.class);

    private static final String WORK_QUEUE_NAME = "MQ_WORK_QUEUE";

    public static void main(String[] args) {
        //获取MQ连接对象
        Connection connection = MQConnecitonUtils.getConnection();
        try {
            //创建消息通道对象
            final Channel channel = connection.createChannel();
            //声明queue队列
            channel.queueDeclare(WORK_QUEUE_NAME, false, false, false, null);

            channel.basicQos(1);

            //创建消费者对象
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //消息消费者获取消息
                    String message = new String(body, StandardCharsets.UTF_8);
                    logger.info("【CustomConsumer02】receive message: " + message);
                    try {
                        Thread.sleep(1000);
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
            channel.basicConsume(WORK_QUEUE_NAME, autoAck, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

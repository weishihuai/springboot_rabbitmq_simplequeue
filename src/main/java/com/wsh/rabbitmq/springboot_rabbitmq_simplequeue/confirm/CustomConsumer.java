package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.confirm;

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
public class CustomConsumer {
    private static Logger logger = LoggerFactory.getLogger(CustomConsumer.class);
//    private static final String QUEUE_NAME = "confirm_producer_queue";
    private static final String QUEUE_NAME = "confirm_producer_queue2";

    public static void main(String[] args) {
        //获取MQ连接对象
        Connection connection = MQConnecitonUtils.getConnection();
        try {
            //创建消息通道对象
            Channel channel = connection.createChannel();
            //创建队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //创建消费者对象
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //消息消费者获取消息
                    String message = new String(body, StandardCharsets.UTF_8);
                    logger.info("【CustomConsumer】receive message: " + message);
                }
            };
            //监听消息队列
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

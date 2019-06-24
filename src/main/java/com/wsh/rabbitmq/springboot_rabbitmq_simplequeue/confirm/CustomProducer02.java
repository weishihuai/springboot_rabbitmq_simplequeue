package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: RabbitMQ confirm机制
 * @Author: weixiaohuai
 * @Date: 2019/6/23
 * @Time: 20:37
 * <p>
 * 说明：
 * 批量发完消息再确认
 */
public class CustomProducer02 {
    private static Logger logger = LoggerFactory.getLogger(CustomProducer02.class);
    private static final String QUEUE_NAME = "confirm_producer_queue";

    public static void main(String[] args) {
        //获取MQ连接
        Connection connection = MQConnecitonUtils.getConnection();
        //从连接中获取Channel通道对象
        Channel channel = null;
        try {
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //开启confirm模式
            channel.confirmSelect();

            //发送消息到交换机exchange上
            String msg = "confirm_producer_queue";
            for (int i = 1; i <= 10; i++) {
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
            }

            //批量发送之后才确认
            if (!channel.waitForConfirms()) {
                logger.error("producer send message failed...");
            } else {
                logger.error("producer send message success...");
            }
        } catch (IOException | InterruptedException e) {
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

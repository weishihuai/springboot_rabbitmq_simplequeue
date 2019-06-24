package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.workqueues.fairdispatch.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: 工作队列 - 消息生产者 (公平分发方式Fair dispatch)
 * @Author: weixiaohuai
 * @Date: 2019/6/23
 * @Time: 10:25
 * <p>
 * 说明：
 * 1. 生产者、消费者指定：channel.basicQos(1);
 * 2. 消费者消费完消息自动发送确认消息：channel.basicAck(envelope.getDeliveryTag(), false);
 * 3. 消费者必须关闭自动应答：autoAck = false;
 * 4. 一般消费者如果处理消息的时间较短，那么它处理的消息会比较多一些;
 */
public class CustomProducer {
    private static final String WORK_QUEUE_NAME = "MQ_WORK_QUEUE";
    private static final String WORK_QUEUE_MESSAGE = "CUSTOMPRODUCER --> WORK_QUEUE_MESSAGE";

    public static void main(String[] args) {
        //获取MQ连接
        Connection connection = MQConnecitonUtils.getConnection();
        //从连接中获取Channel通道对象
        Channel channel = null;
        try {
            channel = connection.createChannel();
            //创建Queue队列
            channel.queueDeclare(WORK_QUEUE_NAME, false, false, false, null);

            //每个消费者发送确认消息之前，消息队列不发送下一个消息到消费者
            channel.basicQos(1);

            //发送10条消息到工作队列
            for (int i = 1; i <= 10; i++) {
                StringBuilder msg = new StringBuilder(WORK_QUEUE_MESSAGE).append(i);
                //发送消息
                channel.basicPublish("", WORK_QUEUE_NAME, null, msg.toString().getBytes());
            }
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

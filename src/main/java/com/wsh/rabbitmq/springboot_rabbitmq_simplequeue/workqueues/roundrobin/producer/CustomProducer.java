package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.workqueues.roundrobin.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: 工作队列 - 消息生产者
 * @Author: weixiaohuai
 * @Date: 2019/6/23
 * @Time: 10:25
 * <p>
 * 说明：
 * 消费者1与消费者2处理的消息是均分的，而且消息是轮训分发的(轮训分发 round-robin)
 */
public class CustomProducer {
    private static final String WORK_QUEUE_NAME = "MQ_WORK_QUEUE";
    private static final String WORK_QUEUE_MESSAGE = "hello world!! ------> ";

    public static void main(String[] args) {
        //获取MQ连接
        Connection connection = MQConnecitonUtils.getConnection();
        //从连接中获取Channel通道对象
        Channel channel = null;
        try {
            channel = connection.createChannel();
            //创建Queue队列
            channel.queueDeclare(WORK_QUEUE_NAME, false, false, false, null);
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

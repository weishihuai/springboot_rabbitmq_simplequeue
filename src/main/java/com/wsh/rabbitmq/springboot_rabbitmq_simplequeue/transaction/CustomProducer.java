package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.transaction;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Description: RabbitMQ事务机制
 * @Author: weixiaohuai
 * @Date: 2019/6/23
 * @Time: 20:37
 * <p>
 * 说明：
 * 这种方式会降低RabbitMQ的吞吐量
 */
public class CustomProducer {
    private static Logger logger = LoggerFactory.getLogger(CustomProducer.class);
    private static final String QUEUE_NAME = "transaction_producer_queue";

    public static void main(String[] args) {
        //获取MQ连接
        Connection connection = MQConnecitonUtils.getConnection();
        //从连接中获取Channel通道对象
        Channel channel = null;
        try {
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //发送消息到交换机exchange上
            String msg = "transaction_producer_queue";
            try {
                //开启事务模式
                channel.txSelect();
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());


                //手动抛一个空指针异常
                //String string = null;
                //string.length();

                //提交事务
                channel.txCommit();
            } catch (Exception e) {
                //回滚事务
                channel.txRollback();
                logger.error("txRollback...");
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

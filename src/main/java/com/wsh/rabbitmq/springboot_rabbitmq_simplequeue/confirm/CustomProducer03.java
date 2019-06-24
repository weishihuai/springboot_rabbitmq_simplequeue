package com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.wsh.rabbitmq.springboot_rabbitmq_simplequeue.utils.MQConnecitonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

/**
 * @Description: RabbitMQ confirm机制
 * @Author: weixiaohuai
 * @Date: 2019/6/23
 * @Time: 20:37
 * <p>
 * 说明：
 * 异步模式
 */
public class CustomProducer03 {
    private static Logger logger = LoggerFactory.getLogger(CustomProducer03.class);
    private static final String QUEUE_NAME = "confirm_producer_queue2";

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

            //未确认的消息标识集合
            final SortedSet<Long> confirmSet = Collections.synchronizedSortedSet(new TreeSet<>());

            //将通道添加一个确认监听事件
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long l, boolean multiple) throws IOException {
                    if (multiple) {
                        logger.info("handleAck...multiple");
                        //将当前seqNo + 1 之前的全部清除
                        confirmSet.headSet(l + 1).clear();
                    } else {
                        logger.info("handleAck...not multiple");
                        confirmSet.remove(l);
                    }
                }

                //handleNack() 可以进行重试操作等
                @Override
                public void handleNack(long l, boolean multiple) throws IOException {
                    if (multiple) {
                        logger.info("handleNack...multiple");
                        //将当前seqNo + 1 之前的全部清除
                        confirmSet.headSet(l + 1).clear();
                    } else {
                        logger.info("handleNack...not multiple");
                        confirmSet.remove(l);
                    }
                }
            });


            //发送消息到交换机exchange上
            String msg = "confirm_producer_queue";
            for (int i = 1; i <= 20; i++) {
                channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
                confirmSet.add(channel.getNextPublishSeqNo());
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

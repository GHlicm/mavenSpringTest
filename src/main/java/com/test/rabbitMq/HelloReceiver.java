package com.test.rabbitMq;

import org.springframework.stereotype.Component;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

@Component
public class HelloReceiver implements ChannelAwareMessageListener{

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
		try {
			 Thread.sleep(2000);
			 System.out.println("睡眠2s");
			 
			 //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉；否则消息服务器以为这条消息没处理掉 后续还会在发
			 channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
			 System.out.println("receiver success = " + new String(message.getBody()));
			 
		 } catch (Exception e) {
			 
			 channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false); //丢弃这条消息
			 System.out.println("receiver fail");
			 
			 e.printStackTrace();
		 }
		
	}

}

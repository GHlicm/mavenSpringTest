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
			 Thread.sleep(8000);
			 System.out.println("睡眠2s");
			 
			 //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉；否则消息服务器以为这条消息没处理掉 后续还会在发
			 /**
			  * 参数解析
			  * deliveryTag：该消息的index;　
			  * multiple：是否批量处理.true:将一次性ack所有小于deliveryTag的消息。
			  */
//			 channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); 
			 /**
			  * 参数解析
			  *	deliveryTag:该消息的index
			  *	multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
			  *	requeue：被拒绝的是否重新入队列
			  */
			 channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
			 System.out.println("receiver success = " + new String(message.getBody()));
			 
		 } catch (Exception e) {
			 
			 channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false); //丢弃这条消息
			 System.out.println("receiver fail");
			 
			 e.printStackTrace();
		 }
		
	}

}

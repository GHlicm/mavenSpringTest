package com.test.rabbitMq;

import org.springframework.stereotype.Component;

import java.util.Date;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class HelloSender implements RabbitTemplate.ConfirmCallback,
	RabbitTemplate.ReturnCallback{
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public void send(String exchange, String routingKey){
		String content = "你好现在是 " + new Date();
		System.out.println("send content = " + content);
		
		this.rabbitTemplate.setMandatory(true);	//如果消息不可达，会发送消息给生产者，生产者通过一个回调函数来获取改信息
		this.rabbitTemplate.setConfirmCallback(this);
		this.rabbitTemplate.setReturnCallback(this);
		this.rabbitTemplate.convertAndSend(exchange, routingKey, content);
	}
	
	/**
	 * 确认后回调
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (ack) {
			System.out.println("send ack success");
		} else {
			System.out.println("send ack fail, cause = " + cause);
		}
	}
	
	/**
	 * 失败后return回调
	 */
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText,
			String exchange, String routingKey) {
		System.out.println("send fail return-message = " + new String(message.getBody())
				+ ", replyCode: " + replyCode
				+ ", replyText: " + replyText
				+ ", exchange: " + exchange
				+ ", routingKey: " + routingKey);
	}

	
	
}

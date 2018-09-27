package com.test.rabbitMq;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.concurrent.ConstantInitializer;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;


@Configuration
public class RabbitMqConfig {
	public final static String queueName  = "hello_queue";
	
	/**
	 * 死信队列：
	*/
	public final static String deadQueueName = "dead_queue";
	public final static String deadRoutingKey = "dead_routing_key";
	public final static String deadExchangeName = "dead_exchange";
	 
	/**
	 * 死信队列 交换机标识符
	*/
	public static final String DEAD_LETTER_QUEUE_KEY = "x-dead-letter-exchange";
	
	/**
	 * 死信队列交换机绑定键标识符
	*/
	public static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
	
	
	@Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory("localhost", 5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPublisherConfirms(true);
        factory.setPublisherReturns(true);
        return factory;
    }
	
	@Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin=new RabbitAdmin(connectionFactory);
        return rabbitAdmin;
    }
	
	@Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
       RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        
       RetryTemplate retryTemplate = new RetryTemplate();
       ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
       backOffPolicy.setInitialInterval(500);
       backOffPolicy.setMultiplier(10.0);
       backOffPolicy.setMaxInterval(10000);
       retryTemplate.setBackOffPolicy(backOffPolicy);
       
       rabbitTemplate.setRetryTemplate(retryTemplate);
//       rabbitTemplate.setMessageConverter(jsonMessageConverter);
       return  rabbitTemplate;
    }
	
//	 @Bean
//	 public MessageConverter jsonMessageConverter() {
//	        return new Jackson2JsonMessageConverter();
//	 }
	
	// 普通队列
	@Bean
	public Queue helloQueue(){
		//将普通队列绑定到死信交换机上(该队列需要绑定两个交换价，还有一个默认的交换机)
		//在消息符合进入死信队列的条件时，进入到:死信交换机→死信队列
		Map<String, Object> args = new HashMap<>(2);
		args.put(DEAD_LETTER_QUEUE_KEY, deadExchangeName);
		args.put(DEAD_LETTER_ROUTING_KEY, deadRoutingKey);
		Queue queue = new Queue(queueName, true, false, false, args);
		return queue;
	}
	
	// 死信队列
	@Bean
	public Queue deadQueue(){
		return new Queue(deadQueueName, true);
	}
	
	// 死信交换机
	@Bean
	public DirectExchange deadExchange(){
		return new DirectExchange(deadExchangeName);
	}
	
	// 死信队列和死信交换机的绑定
	@Bean
	public Binding bindingDeadExchange(Queue deadQueue, DirectExchange deadExchange){
		return BindingBuilder.bind(deadQueue).to(deadExchange).with(deadRoutingKey);
	}
	
	@Bean
	public SimpleMessageListenerContainer listenerContainer() {		
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueueNames(this.queueName);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		container.setMessageListener(new MessageListenerAdapter(new HelloReceiver()));		
		return container;	
	}

}

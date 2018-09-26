package com.test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mapper.DistributeLockMapper;
import com.test.rabbitMq.HelloSender;
import com.test.rabbitMq.RabbitMqConfig;
import com.util.lockUtil.MysqlLock;
import com.util.lockUtil.ZkLock;
import com.util.lockUtil.redisLock.RedisLock;

import redis.clients.jedis.Jedis;


@Controller
@RequestMapping("/llo")
public class HelloSpringController {
//	@Autowired
//	private MysqlLock mysqlLock;
//	@Autowired
//	private RedisLock redisLock;
//	@Autowired
//	private ZkLock zkLock;
	
	@Autowired
	private HelloSender sender;
	
	@ResponseBody
	@RequestMapping("/test")
	public String test(){
		sender.send(null, RabbitMqConfig.queueName);
		return "test";
	}
	
	@ResponseBody
	@RequestMapping("/test1")
	public String test1() throws IOException{
		return "test1";
	}
	
	@ResponseBody
	@RequestMapping("/1")
	public String getStr1() throws InterruptedException{
		int count = 100;
//		
//		Ticket ticket = new Ticket(count, mysqlLock);
//		
//		Thread t1 = new Thread(ticket, "窗口A");
//		Thread t2 = new Thread(ticket, "窗口B");
//		Thread t3 = new Thread(ticket, "窗口C");
//		Thread t4 = new Thread(ticket, "窗口D");
//		
//		t1.start();
//		t2.start();
//		t3.start();
//		t4.start();
		
		Thread.currentThread().join();
		
		return "sssssssssssssss";
	}
	
	@ResponseBody
	@RequestMapping("/2")
	public String getStr2() throws InterruptedException{
		int count = 100;
		
//		Ticket ticket = new Ticket(count, redisLock);
//		
//		Thread t1 = new Thread(ticket, "窗口A");
//		Thread t2 = new Thread(ticket, "窗口B");
//		Thread t3 = new Thread(ticket, "窗口C");
//		Thread t4 = new Thread(ticket, "窗口D");
//		
//		t1.start();
//		t2.start();
//		t3.start();
//		t4.start();
//		
//		Thread.currentThread().join();
		
		return "sssssssssssssss";
	}
	
	@ResponseBody
	@RequestMapping("/3")
	public String getStr3() throws InterruptedException{
		int count = 100;
		
		Ticket ticket = new Ticket(count);
		
		Thread t1 = new Thread(ticket, "窗口A");
		Thread t2 = new Thread(ticket, "窗口B");
		Thread t3 = new Thread(ticket, "窗口C");
		Thread t4 = new Thread(ticket, "窗口D");
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
		Thread.currentThread().join();
		
		return "3";
	}
	
}

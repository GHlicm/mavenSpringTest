package com.util.lockUtil.redisLock;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
@Component
public class RedisLock implements Lock{
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;
	
	private static final String LOCK = "lock";
	
	ThreadLocal<String> local = new ThreadLocal<String>();
	
	@Override
	public void lock() {
		if (!tryLock()) {
			try {
				Thread.sleep(new Random().nextInt(10)+1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock();
		}
	}

	@Override
	public boolean tryLock() {
		Jedis jedis = null;
		try {
			jedis = (Jedis)jedisConnectionFactory.getConnection().getNativeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String value = UUID.randomUUID().toString();
		local.set(value);
		String ret = jedis.set(LOCK, value, "NX", "PX", 3000);
		if ("OK".equals(ret)) {
			return true;
		}
		return false;
	}
	
	@Override
	public void unlock() {
		try {
			String script = FileUtils.readFileToString(new File("D:/Workspace/mavenSpringTest/src/main/java/com/util/lockUtil/redisLock/unlock.lua"));
			Jedis jedis = (Jedis)jedisConnectionFactory.getConnection().getNativeConnection();
			jedis.eval(script, Arrays.asList(LOCK), Arrays.asList(local.get()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}

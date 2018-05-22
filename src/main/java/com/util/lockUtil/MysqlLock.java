package com.util.lockUtil;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mapper.DistributeLockMapper;

@Component
public class MysqlLock implements Lock{
	
	private static final int NUM = 1;
	
	@Autowired
	private DistributeLockMapper distributeLockMapper;
	
	@Override
	public void lock() {
		if (!tryLock()) {
			try{
				Thread.sleep(new Random().nextInt(10)+1);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		lock();
	}

	@Override
	public boolean tryLock() {
		try {
			distributeLockMapper.insert(NUM);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public void unlock() {
		try {
			distributeLockMapper.delete(NUM);
		} catch (Exception e) {
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

package com.util.lockUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.springframework.data.redis.PassThroughExceptionTranslationStrategy;
import org.springframework.stereotype.Component;

@Component
public class ZkLock implements Lock{
	
	private static final String LOCK_PATH = "/LOCK";
	
	private static final String ZOOKEEPER_IP_PORT = "localhost:2181";
	
	private ZkClient client = new ZkClient(ZOOKEEPER_IP_PORT, 1000, 1000, new SerializableSerializer());
	
	private CountDownLatch cdl;
	
	private String beforePath;	//当前请求的节点
	private String currentPath;	//当前请求节点的前一个节点
	
	public ZkLock() {
		//判断有没有LOCK目录，没有则创建
		if (!this.client.exists(LOCK_PATH)) {
			this.client.createPersistent(LOCK_PATH);
		}
	}
	
	@Override
	public boolean tryLock() {
		//如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
		if (currentPath == null) {
			//创建一个临时顺序节点
			currentPath = this.client.createEphemeralSequential(LOCK_PATH + "/", "lock");
//			System.out.println("---------------------------->" + currentPath);
		}
		
		List<String> childrens = this.client.getChildren(LOCK_PATH);
		Collections.sort(childrens);
		
		if (currentPath.equals(LOCK_PATH + "/" + childrens.get(0))) {// 如果当前节点在所有节点中排名第一则获取锁成功
			return true;
		} else {// 如果排名不是第一，则获取前面的节点名称，并赋值给beforePath
			int wz = Collections.binarySearch(childrens, currentPath.substring(6));
			beforePath = LOCK_PATH + "/" + childrens.get(wz - 1);
		}
		
		return false;
	}
	
	@Override
	public void lock() {
		if (!tryLock()) {
			waitForLock();
			lock();
		} else {
//			System.out.println(Thread.currentThread().getName() + "获得分布式锁！");
		}
	}
	
	private void waitForLock(){
		IZkDataListener listener = new IZkDataListener(){
			//捕获到前置节点被删除的事件后，发令枪countdown让主线程停止等待
			 public void handleDataDeleted(String dataPath) throws Exception {
				 if (cdl != null) {
					cdl.countDown();
				}
			 }
			 
			 public void handleDataChange(String dataPath, Object data) throws Exception {
				 
			 }
		};
		
		//给排在前面的节点增加数据删除的watcher，本质是启动另外一个线程去监听前置节点
		this.client.subscribeDataChanges(beforePath, listener);
		
		if (this.client.exists(beforePath)) {
			cdl = new CountDownLatch(1);
			try{
				cdl.await();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		this.client.unsubscribeDataChanges(beforePath, listener);
	}
	
	@Override
	public void unlock() {
		//删除当前临时节点
		this.client.delete(currentPath);
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

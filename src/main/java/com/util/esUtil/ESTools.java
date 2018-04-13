package com.util.esUtil;

import java.net.InetAddress;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ESTools {
	
	public final static Client client =  build();
	public final static String ip = "192.168.77.17";
	
	/**
	 * 创建一次
	 * @return
	 */
	private static Client build(){
		if(null != client){
			return client;
		}
		Client client = null;
		try {
			System.out.println("创建client开始");
			
			Settings settings = Settings
				.settingsBuilder()
					.put("cluster.name","my-application")	//设置es实例的名称
						.put("client.transport.sniff", true) //自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
							.build();
			client = TransportClient.builder().settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), 9300)); //此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
			
			System.out.println("创建client结束！");
		} catch (Exception e) {
			System.out.println("创建client异常！");
			e.printStackTrace();
		}
		return client;
	}
	
	/**
	 * 关闭
	 */
	public static void close(){
		if(null != client){
			try {
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建索引
	 */
	public static void createMyIndex(String index){
		CreateIndexRequest request = new CreateIndexRequest(index);
		ESTools.client.admin().indices().create(request);
	}
	
	/**
	 * 创建mapping
	 */
	public static void createMyMapping(String index, String type){
	    PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(MyMapping.getMapping());
	    ESTools.client.admin().indices().putMapping(mapping).actionGet();
	}
}

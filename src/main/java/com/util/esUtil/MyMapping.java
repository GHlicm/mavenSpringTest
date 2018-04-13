package com.util.esUtil;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class MyMapping {
	
	public static XContentBuilder getMapping(){
		XContentBuilder mapping = null;
		try {
			mapping = XContentFactory.jsonBuilder()
			.startObject()
				//开启倒计时功能
				.startObject("_ttl")
					.field("enabled",false)
				.endObject()
				.startObject("properties")
					.startObject("id")
						.field("type","long")
					.endObject()
					.startObject("title")
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject("Author")
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject("content")
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
					.startObject("postedat")
						.field("type","string")
						.field("index","not_analyzed")
					.endObject()
				.endObject()
			.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mapping;
	}
	
}

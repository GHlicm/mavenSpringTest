package com.util.esUtil;

import net.sf.json.JSONObject;

public class ETest {
	public static void main(String[] args) throws InterruptedException {
		String index = "blog";
		String type = "articles";
		String idName = null;
		
//		ESTools.createMyIndex(index);
//		Thread.sleep(2000); //建索引需要一定的时间，否则构建type会出错
//		ESTools.createMyMapping(index, type);
		
		for(int i=1; i<10; i++){

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", i);
			jsonObj.put("title", i+"散文");
			jsonObj.put("Author", i+"悦光阴");
			jsonObj.put("content", i+"xxxxxxxxxxx");
			jsonObj.put("postedat", "2017-03-01");
	
			HandleManager.save(index, type, idName, jsonObj);
		}
		
//		HandleManager.save(index, type, idName, jsonObj);
//			HandleManager.deleteRowByKey(index, type, "");
//		System.out.println(HandleManager.findRowById("5", index, type));
	}
}

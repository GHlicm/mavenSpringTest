package com.util.esUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import net.sf.json.JSONObject;

public class HandleManager {

	/**
	 * 添加数据到Elasticsearch
	 * 
	 * @param index
	 *            索引
	 * @param type
	 *            类型
	 * @param idName
	 *            Id字段名称
	 * @param json
	 *            存储的JSON，可以接受Map
	 * @return
	 */
	public static Map save(String index, String type, String idName, JSONObject json) {
		List list = new ArrayList();
		list.add(json);
		return save(index, type, idName, list);
	}

	/**
	 * 添加数据到Elasticsearch
	 * 
	 * @param index
	 *            索引
	 * @param type
	 *            类型
	 * @param idName
	 *            Id字段名称
	 * @param listData
	 *            一个对象集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map save(String index, String type, String idName, List listData) {
		BulkRequestBuilder bulkRequest = ESTools.client.prepareBulk().setRefresh(true);
		Map resultMap = new HashMap();

		for (Object object : listData) {
			JSONObject json = JSONObject.fromObject(object);
			// 没有指定idName 那就让Elasticsearch自动生成
			if (StringUtils.isBlank(idName)) {
				IndexRequestBuilder lrb = ESTools.client.prepareIndex(index, type).setSource(json);
				bulkRequest.add(lrb);
			} else {
				String idValue = json.optString(idName);
				IndexRequestBuilder lrb = ESTools.client.prepareIndex(index, type, idValue).setSource(json);
				bulkRequest.add(lrb);
			}
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();

		if (bulkResponse.hasFailures()) {
			// process failures by iterating through each bulk response item
			System.out.println(bulkResponse.getItems().toString());
			resultMap.put("500", "保存ES失败!");
			return resultMap;
		}
		bulkRequest = ESTools.client.prepareBulk();
		resultMap.put("200", "保存ES成功!");
		return resultMap;
	}

	/**
	 * 根据ID删除
	 * 
	 * @param id
	 * @return
	 */
	public static int deleteRowByKey(String index, String type, String id) {

		DeleteResponse result = ESTools.client.prepareDelete()
				.setRefresh(true)
				.setIndex(index)
				.setType(type)
				.setId(id) // ID指的是"_id"
				.setRefresh(true) // 刷新
				.execute().actionGet();
		// 是否查找并删除
		boolean isfound = result.isFound();
		return isfound ? 1 : 0;
	}

	/**
	 * 根据Id 查询 
	 * 
	 * @param key
	 * @return
	 */
	public static List findRowById(String id, String index, String type) {
		Client client = ESTools.client;
		SearchResponse response = client.prepareSearch(index)
				.setTypes(type)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.termQuery("id", id)) // Query
//				.setQuery(QueryBuilders.termsQuery("id", "2", "1")) // 匹配id为1和2的记录
//				.setQuery(QueryBuilders.boolQuery().should(QueryBuilders.termQuery("id", "1"))
//						.should(QueryBuilders.termQuery("title", "2散文"))) //should代表or操作
				.setPostFilter(QueryBuilders.rangeQuery("id").from(2).to(8)) // Filter
//				.setFrom(0).setSize(30) //分页查询 
				.setExplain(true).execute().actionGet();
		SearchHits hits = response.getHits();
		List list = new ArrayList();
		for (SearchHit searchHit : hits) {
			Map source = searchHit.getSource();
//			SOBangg entity = (SOBangg) JSONObject.toBean(JSONObject.fromObject(source), SOBangg.class);
//			list.add(entity);
			list.add(source);
		}
		return list;
	}
}

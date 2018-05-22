package com.test;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mapper.DistributeLockMapper;
import com.util.lockUtil.MysqlLock;


@Controller
@RequestMapping("/llo")
public class HelloSpringController {
	@Autowired
	private MysqlLock mysqlLock;
	
	@ResponseBody
	@RequestMapping("/123")
	public String getStr(){
		System.out.println("xxxxxxxxxxxxxxxxxxxx");
		A a = new A();
		a.setS("string");
		
		mysqlLock.unlock();
		
		return "sssssssssssssss";
	}
	
	public static void main(String[] args) {
		try {
			Reader reader = Resources.getResourceAsReader("mybatis-conf.xml");
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			SqlSession session = sqlSessionFactory.openSession();
			//TODO ...........突然想到用tomcat启动加载
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/llo")
public class HelloSpringController {
	
	@ResponseBody
	@RequestMapping("/123")
	public String getStr(){
		System.out.println("xxxxxxxxxxxxxxxxxxxx");
		A a = new A();
		a.setS("string");
		return "sssssssssssssss";
	}
}

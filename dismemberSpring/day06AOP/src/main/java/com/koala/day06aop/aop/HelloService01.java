package com.koala.day06aop.aop;

import org.springframework.stereotype.Component;

/**
 * day24
 * Create by koala on 2021-07-18
 */
@Component  //day25：切面存在的话就会返回代理对象
public class HelloService01 {

	public HelloService01(){
		System.out.println("....");
	}

	public String sayHello(String name){
		String result = "你好："+name;
		System.out.println(result);
		int length = name.length();
		return result + "---" + length;
	}

}

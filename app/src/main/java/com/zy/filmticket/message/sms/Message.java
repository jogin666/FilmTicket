package com.zy.filmticket.message.sms;

import java.util.HashMap;
import java.util.Map;

public class Message 
{
	private final String id="jogin";
	private final String key="d41d8cd98f00b204e980";
	private String content=null; //消息的内容
	private String phoneNum=null; //接受短信的手机号码

	//
	public Message(String content,String phoneNum) {
		this.content=content;
		this.phoneNum=phoneNum;
	}

	//
	public Map<String, String> getMap() {
		Map<String,String> map=new HashMap<String,String>();
		map.put("Uid", id);
		map.put("Key", key);
		map.put("smsMob", phoneNum);
		map.put("smsText", content);
		return map;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
}

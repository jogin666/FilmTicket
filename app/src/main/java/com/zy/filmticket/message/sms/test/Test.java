package com.zy.filmticket.message.sms.test;

import java.util.Scanner;

import com.zy.filmticket.message.sms.Message;
import com.zy.filmticket.message.sms.SendSMS;

public class Test
{
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		SendSMS smp=new SendSMS();
		Scanner scan=new Scanner(System.in);
		System.out.println("接受短信的手机号：");
		String phone=scan.nextLine();
		System.out.println("短信的内容：");
		String content=scan.nextLine();
		System.out.println("短信编码方式：");
		String type=scan.nextLine();
		Message message=new Message(content,phone);
		smp.sendMessagePost(message, type);
	}
}

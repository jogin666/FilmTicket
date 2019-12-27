package com.zy.filmticket.message.sms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class SendSMS {

    private static SendSMS instance=null;

    //设置请求参数
    private RequestConfig requestConfing=RequestConfig.custom()
            .setSocketTimeout(1500)//通信时间
            .setConnectTimeout(1500) //连接时间
            .setConnectionRequestTimeout(1500)//请求连接时间
            .build();

    //默认构造函数
    public SendSMS() {
    }

    //获取SendMobPhoneMessage实例
    public static SendSMS getSendMobPhoneMessage() {
        if (instance==null) {
            instance = new SendSMS();
        }
        return instance;
    }

    //发送信息(post)
    public int sendMessagePost(Message message, String type) {
        String url="http://utf8.sms.webchinese.cn";
        String result= sendHttpPost(url, message.getMap(), type);
        return Integer.parseInt(result);
    }

    //发送信息(post)
    public int sendMessagePost(String httpUrl_Content,String type) {
        String result=this.sendHttpPost(httpUrl_Content, type);
        return Integer.parseInt(result);
    }

    //发送信息(get)
    public int sendMessageGet(String httpUrl_Content,String type) {
        HttpGet httpGet=new HttpGet(httpUrl_Content);
        String result=sendHttpGet(httpGet, type);
        return Integer.parseInt(result);
    }

    //发送信息(get Https)
    public int sendMessageGets(String httpUrl_Content,String type)
    {
        HttpGet httpGet=new HttpGet(httpUrl_Content);
        String result=sendHttpsGet(httpGet, type);
        return Integer.parseInt(result);
    }

    //发送post请求
    public String sendHttpPost(String httpUrl,Map<String,String>map,String type) {
        HttpPost httpPost=new HttpPost(httpUrl);
        List <NameValuePair> nameValuePair=new ArrayList<NameValuePair>();
        for(Map.Entry<String, String> entry:map.entrySet()) {
            NameValuePair nvp=new BasicNameValuePair(entry.getKey(),entry.getValue());
            nameValuePair.add(nvp);
        }
        try {
            UrlEncodedFormEntity entity=new UrlEncodedFormEntity(nameValuePair,type);
            httpPost.setEntity(entity);
        } catch(UnsupportedEncodingException e) {
            System.out.println("编码出现异常："+e.getMessage());
        }
        return getOrPost(null,httpPost,type);
    }

    //发送post请求
    public String sendHttpPost(String httpUrl,String type) {
        /**
         * httpUrl 编码发送接口地址和短信的所有参数构成
         * 列如：http://gbk.api.smschinese.cn/?Uid=本站用户名&Key=接口安全秘钥&smsMob=手机号码&smsText=验证码:8888
         */
        HttpPost httpPost=new HttpPost(httpUrl);
        return getOrPost(null,httpPost,type);
    }

    //发送get请求(http)
    public String sendHttpGet(HttpGet httpGet,String type)
    {
        return getOrPost(httpGet, null, type);
    }

	//
    public String getOrPost(HttpGet httpGet,HttpPost httpPost,String type) {
        CloseableHttpClient httpClient=null; //执行请求对象
        CloseableHttpResponse httpResponse=null;  //执行请求响应对象
        HttpEntity entity=null; //短信实体对象
        String responseContent=null; //发送消息结果
        try {
            httpGet.setConfig(requestConfing); //设置请求参数
            httpClient=HttpClients.createDefault(); //获取执行get请求的对象，默认的httpClient实例.
            if(httpPost!=null) {
                httpResponse=httpClient.execute(httpPost); //执行请求返回，响应的对象
            } else {
                httpResponse=httpClient.execute(httpGet); //执行请求返回，响应的对象
            }
            entity=httpResponse.getEntity();//获取短信内容的实体
            responseContent=EntityUtils.toString(entity,type); //发送短信，返回发送结果
        } catch(ClientProtocolException e) {
            System.out.println("短信协议有误："+e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("发送短信失败："+e.getMessage());
        } finally {
            close(httpClient, httpResponse);
        }
        return responseContent;
	}

    //发送 get请求(https)
	public String sendHttpsGet(HttpGet httpGet,String type) {
        CloseableHttpClient httpClient=null; //执行请求对象
        CloseableHttpResponse httpResponse=null;  //执行请求响应对象
        HttpEntity entity=null; //短信实体对象
        String responseContent=null; //发送消息结果
		try {
			PublicSuffixMatcher psm=PublicSuffixMatcherLoader.load(new URL(httpGet.getURI().toString()));
			DefaultHostnameVerifier dhnv=new DefaultHostnameVerifier(psm);	
			httpClient=HttpClients.custom().setSSLHostnameVerifier(dhnv).build();
			httpGet.setConfig(requestConfing);
			httpResponse=httpClient.execute(httpGet);
			entity=httpResponse.getEntity();
			responseContent=EntityUtils.toString(entity);
		}
		catch(MalformedURLException e) {
            System.out.println("短信发送失败，错误的地址号："+e.getMessage());
		}
		catch(IOException e) {
            System.out.println("短信发送失败，错误的地址号："+e.getMessage());
		}
		finally {
			this.close(httpClient, httpResponse);
		}
		return responseContent;
	}

    //关闭请求，断开连接
	public void close(CloseableHttpClient httpClient,CloseableHttpResponse httpResponse) {
		try {
			if(httpResponse!=null) {
				httpResponse.close();
			}
			if(httpClient!=null) {
				httpClient.close();
			}
		} catch(IOException e) {
            System.out.println("发送短信失败："+e.getMessage());
		}
	}

	//返回异常原因
	public String getErrorMsg(int errorCode) {
		if(errorCode==-1){
			return "没有该用户账户";
		}else if(errorCode==-2){
			return "接口密钥不正确";
		}else if(errorCode==-3){
			return "短信数量不足";
		}else if(errorCode==-4){
			return "手机号格式不正确";
		}else if(errorCode==-21){
			return "MD5接口密钥加密不正确";
		}else if(errorCode==-11){
			return "该用户被禁用";
		}else if(errorCode==-14){
			return "短信内容出现非法字符";
		}else if(errorCode==-41){
			return "手机号码为空";
		}else if(errorCode==-42){
			return "短信内容为空";
		}else if(errorCode==-51){
			return "短信签名格式不正确";
		}else if(errorCode==-6){
			return "IP限制";
		}else{
			return "未知错误码:"+errorCode;
		}
	}

}

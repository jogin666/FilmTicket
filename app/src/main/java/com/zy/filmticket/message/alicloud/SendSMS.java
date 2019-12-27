package com.zy.filmticket.message.alicloud;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import java.util.Date;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.profile.DefaultProfile;


import java.io.BufferedReader;
import java.io.InputStreamReader;


public class SendSMS {

    //产品名称:云通信短信API产品,开发者无需替换
    private static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    private static final String domain = "dysmsapi.aliyuncs.com";
    //秘钥
    private static final String accessKeyId="LTAIFgUjsL0tTmoU";
    private static final String accessKeySecret="sZBj5zx2CNSdLOoNSi49lPTrQa4cJZ";
    //签名
    private static final String signName="FilmTicket";
    //模板code
    private static final String templateCode="SMS_165414935";


    //发送短信
    public static SendSmsResponse sendSms(String phoneNum, String codeNum) throws ClientException {
        IAcsClient acsClient = new DefaultAcsClient(buildProfile());
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //待发送手机号
        request.setPhoneNumbers(phoneNum);
        //签名
        request.setSignName(signName);
        //短信模板
        request.setTemplateCode(templateCode);
        //短信验证码
        request.setTemplateParam("{\"code\":'"+codeNum+"'}");
        //行短信扩展码 （回复）
        //request.setSmsUpExtendCode("90997");
        //为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");
        //此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse=null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return sendSmsResponse;
    }

    //设置配置
    private static IClientProfile buildProfile(){
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000"); //默认连接时间
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");  //读取连接时间

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        }catch (Exception e){
            e.printStackTrace();
        }
        return profile;
    }

    //设置回显短信的参数
    public static QuerySendDetailsResponse querySendDetails(String bizId,String phoneNum) throws ClientException {

        IAcsClient acsClient = new DefaultAcsClient(buildProfile());

        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber(phoneNum);
        //可选-流水号
        request.setBizId(bizId);
        //发送日期 支持30天内记录查询
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        //页大小
        request.setPageSize(10L);
        //当前页码从1开始计数
        request.setCurrentPage(1L);
        QuerySendDetailsResponse querySendDetailsResponse=null;
        try {
            querySendDetailsResponse = acsClient.getAcsResponse(request);
        }catch (Exception e){
            e.printStackTrace();
        }
        return querySendDetailsResponse;
    }

    //发送短信和回显短信数据
    public static void sendSMS(String phoneNum,String codeNum) {

        try {
            //发短信
            SendSmsResponse response = sendSms(phoneNum,codeNum);
            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());

            Thread.sleep(3000L); //线程休眠

            //查明细
            if (response.getCode() != null && response.getCode().equals("OK")) {
                QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(response.getBizId(), phoneNum);
                System.out.println("短信明细查询接口返回数据----------------");
                System.out.println("Code=" + querySendDetailsResponse.getCode());
                System.out.println("Message=" + querySendDetailsResponse.getMessage());
                int i = 0;
                for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs()) {
                    System.out.println("SmsSendDetailDTO[" + i + "]:");
                    System.out.println("Content=" + smsSendDetailDTO.getContent());
                    System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
                    System.out.println("OutId=" + smsSendDetailDTO.getOutId());
                    System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
                    System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
                    System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
                    System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
                    System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
                }
                System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
                System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());
            }
        }catch(ClientException |InterruptedException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //发送短信
    public static void sendSmms(String phone, String code) throws Exception {

        //重新
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new java.util.SimpleTimeZone(0, "POST"));
        java.util.Map<String, String> paras = new java.util.HashMap<String, String>();
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
        paras.put("AccessKeyId", accessKeyId);
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", df.format(new java.util.Date()));
        paras.put("Format", "XML");
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-hangzhou");
        paras.put("PhoneNumbers", phone);
        paras.put("SignName", signName);
        paras.put("TemplateParam", "{\"code\": '"+ code + "'}");//这里根据具体情况而定
        paras.put("TemplateCode", templateCode);
        paras.put("OutId", "123");
        if (paras.containsKey("Signature"))
            paras.remove("Signature");
        java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<String, String>();
        sortParas.putAll(paras);
        java.util.Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
        }
        String sortedQueryString = sortQueryStringTmp.substring(1);
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append("GET").append("&");
        stringToSign.append(specialUrlEncode("/")).append("&");
        stringToSign.append(specialUrlEncode(sortedQueryString));
        String sign = sign(accessKeySecret + "&", stringToSign.toString());
        String signature = specialUrlEncode(sign);
        String u = "http://dysmsapi.aliyuncs.com/?Signature=" + signature + sortQueryStringTmp;
        System.out.println(u);
        try {
            URL url = new URL(u);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            connection.disconnect();
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("请求失败!");
        }
        System.out.println("发送成功了，手机号码为："+phone+"短信验证码为："+code);
    }

    //重构请求路劲
    public static String specialUrlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20")
                .replace("*", "%2A").replace("%7E", "~");
    }

    //重建签名
    public static String sign(String accessSecret, String stringToSign) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return new sun.misc.BASE64Encoder().encode(signData);
    }

}

package com.zy.filmticket.filmticketService;

import com.zy.filmticket.entity.CinemaEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CinemaService {

    public static List<CinemaEntity> findCinemaByCityName(String responseText){
        return handleResponseText(responseText);
    }

    //通过城市名，获取该城市电影院的id
    public static String findCinemaIDByCityName(String responseText){
        return handleCinemIdResponseText(responseText);
    }

    //通过上映电影的id 播放该电影的电影院id
    public static String findCinemIdByFilmId(String responseText){
        return handleCinemIdResponseText(responseText);
    }

    public static String handleCinemIdResponseText(String responseText){
        try{
            JSONArray array=new JSONArray(responseText);
            StringBuffer stringBuffer=new StringBuffer();
            for (int i=0;i<array.length();i++){
                System.out.println(responseText);
                JSONObject object=array.getJSONObject(i);
                if (stringBuffer.length()>0){
                    stringBuffer.append(",");
                }
                String cinemaId=object.getString("cinemaId");
                stringBuffer.append(cinemaId);
            }
            return stringBuffer.toString();
        }catch (JSONException e){
            System.out.println("解释失败！");
            e.printStackTrace();
        }
        return null;
    }


    //通过电影院名获取电影院的信息
    public static List<CinemaEntity> findCinemaByName(String responseText){
        return handleResponseText(responseText);
    }

    //通过电影院的di获取电影院的信息
    public List<CinemaEntity> findCinemaByCinemaIds(String responseText){
        return handleResponseText(responseText);
    }

    public static List<CinemaEntity> handleResponseText(String responseText){
        try{
            JSONArray array=new JSONArray(responseText);
            List<CinemaEntity> list=new ArrayList<>();
            for (int i=0;i<array.length();i++){
                JSONObject object=array.getJSONObject(i);
                String cinemaId=object.getString("cinemaId");
                String cinemaName=object.getString("cinemaName");
                String city=object.getString("cinemaCity");
                String cinemaAddress=object.getString("cinemaAddress");
                CinemaEntity entity=new CinemaEntity(cinemaId,cinemaName,city,cinemaAddress);
                list.add(entity);
            }
            return list;
        }catch (JSONException e){
            System.out.println("解释失败！");
            e.printStackTrace();
        }
        return null;
    }
}

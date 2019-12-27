package com.zy.filmticket.location;

import android.text.TextUtils;

import com.zy.filmticket.location.pojo.City;
import com.zy.filmticket.location.pojo.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationService {

    //解释和处理服务器返回的省级数据
    public static List<Province> handleProvinceResponse(String responseText){
        List<Province> provinceList=new ArrayList<Province>();
        try {
            if (!TextUtils.isEmpty(responseText)) {
                JSONArray allProvince = new JSONArray(responseText);
                for (int i = 0; i < allProvince.length();i++){
                    JSONObject provinceObject=allProvince.getJSONObject(i);
                    System.out.println("provinceObject->"+provinceObject);
                    System.out.println("array->");
                    Province province=new Province();
                    province.setPrivinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    provinceList.add(province);
                }
            }
        }catch(JSONException e){
            System.out.println("解释失败！");
            e.printStackTrace();
        }
        return provinceList;
    }

    //解释和处理服务器返回的市级数据
    public static List<City> handleCityResponse(String responseText,int provinceId){
        List<City> cityList=new ArrayList<City>();
        try{
            if(!TextUtils.isEmpty(responseText)){
                JSONArray cities=new JSONArray(responseText);
                for (int i=0;i<cities.length();i++){
                    JSONObject cityObject=cities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    cityList.add(city);
                }
            }
        }catch(JSONException e){
            System.out.println("解释失败！");
            e.printStackTrace();
        }
        return cityList;
    }

}

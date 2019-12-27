package com.zy.filmticket.filmticketService;

import com.zy.filmticket.entity.FilmReleaseEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReleaseFilmService {

    public static String findFilmIds(String responseText){
        try{
            JSONArray array=new JSONArray(responseText);
            String filmIds="";
            for (int i=0;i<array.length();i++){
                JSONObject object=array.getJSONObject(i);
                String releaseFilmId=object.getString("filmId");
                if (filmIds.length()>0 && !filmIds.contains(releaseFilmId)){
                    filmIds=filmIds+",";
                }
                if (!filmIds.contains(releaseFilmId)) {
                   filmIds=filmIds + releaseFilmId;
                }
            }
            return filmIds;
        }catch (JSONException e){
            System.out.println("解释失败！");
            e.printStackTrace();
        }
        return null;
    }

    public static List<FilmReleaseEntity> findReleaseFilmFyCinemaIdAndFilmId(String resonseText){
        return handleResponseText(resonseText);
    }

    public static List<FilmReleaseEntity> findReleaseFilmByCinemaId(String responseText){
        return handleResponseText(responseText);
    }


    public static List<FilmReleaseEntity> findReleaseFilmByFilmId(String responseText){
        return handleResponseText(responseText);
    }

    public static List<FilmReleaseEntity> findFilmReleasentityByFilmName(String responseText){
        return handleResponseText(responseText);
    }

    public static List<FilmReleaseEntity> handleResponseText(String responseText){
        try{
            JSONArray array=new JSONArray(responseText);
            List<FilmReleaseEntity> list=new ArrayList<>();
            for (int i=0;i<array.length();i++){
                JSONObject object=array.getJSONObject(i);
                int id=object.getInt("id");
                String filmId=object.getString("filmId");
                String cinemaId=object.getString("cinemaId");
                String releaseDate=object.getString("releaseDate");
                int releasePosition=object.getInt("releasePosition");
                String cinemaName=object.getString("cinemaName");
                String releaseTime=object.getString("releaseTime");
                String filmName=object.getString("filmName");
                int releaseNum=object.getInt("releaseNum");
                FilmReleaseEntity entity=new FilmReleaseEntity(id,filmId,cinemaId,releaseDate,
                        releasePosition,cinemaName,releaseTime,filmName,releaseNum);
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

package com.zy.filmticket.filmticketService;

import android.text.TextUtils;

import com.zy.filmticket.entity.FilmEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilmService {

    public static List<FilmEntity> findFilms(String responseText){
        return  handleResponseText(responseText);
    }

    public static List<FilmEntity> findFilmByCityName(String responseText){
        return  handleResponseText(responseText);
    }

    public static List<FilmEntity> findFilmbyFilmId(String responseText){
        return  handleResponseText(responseText);
    }

    private static List<FilmEntity> handleResponseText(String responseText){
        if (!TextUtils.isEmpty(responseText)){
            try {
                JSONArray films=new JSONArray(responseText);
                List<FilmEntity> list=new ArrayList<>();
                for (int i=0;i<films.length();i++){
                    JSONObject object=films.getJSONObject(i);
                    Double rating=object.getDouble("rating");
                    String description=object.getString("description");
                    String filmName=object.getString("filmName");
                    String genres=object.getString("genres");
                    int durationMin=object.getInt("durationMin");
                    String language=object.getString("language");
                    String releaseDates=object.getString("releaseDates");
                    String countries=object.getString("countries");
                    String coverUrl=object.getString("coverUrl");
                    String screenTypes=object.getString("screenTypes");
                    String filmId=object.getString("filmId");
                    FilmEntity entity=new FilmEntity(rating,description,filmName,genres,durationMin,
                            language,releaseDates,countries,coverUrl,screenTypes,filmId);
                    list.add(entity);

                }
                return list;
            }catch (JSONException e){
                System.out.println("解析失败！");
                e.printStackTrace();
            }
        }
        return null;
    }
}

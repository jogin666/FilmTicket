package com.zy.filmticket.filmticketService;

public class HttpUrl {
    //服务器请求路径
    public static final String filmTicketUrl="http://192.168.1.107:8080/filmticket/";

    //*****************************用户信息
    //查询用户信息
    public static final String findUserInfo="findUserInfo";
    //更新用户信息
    public static final String updateUserInfo="updateUserInfo";
    //判断用户是否存在
    public static final String isExitUser="isExitUser";
    //更改密码
    public static final String updateUserPassword="updateUserPassword";
    //增加用户
    public static final String addUser="addUser";
    //登录
    public static final String login="userLogin";


    //*****************************电影
    //查找所用电影
    public static final String findFilms="findFilms";
    //查找电影通过城市名
    public static final String findFilmByName="findFilmByName";
    //查找电影通过电影Id
    public static final String findFilmById="findFilmById";



    //*****************************电影院
    //查找电影院by城市名
    public static final String findCinemaByCityName="findCinemaByCityName";
    //获取电影院的Idby城市名
    public static final String getCinemaIdsByCityName="getCinemaIdsByCityName";
    //获取电影院信息by电影院名
    public static final String findCinemaByName="findCinemaByName";
    //通过cinemaId获取电影院信息
    public static final String findCinemaByCinemaId="findCinemaByCinemaId";



    //*****************************上映电影信息
    //查找上映电影信息bycinemaId
    public static final String findReleaseFilmByCinemaId="releaseFilm/findFilmByCinemaId";
    //by cinemaId  获取上映电影的id
    public static final String findReleaseByFilmIds="releaseFilm/findReleaseFilmIds";
    //by id 获取上映电影信息
    public static final String findReleaseFilmById="releaseFilm/findReleaseFilmById";
    //by filmName 获取上映的电影信息
    public static final String findReleaseByFilmName="releaseFilm/findReleaseByFilmName";
    //通过上映电影的id获取电影院的cinemaId
    public static final String findCinemaIdByFilmId="releaseFilm/findCinemaIdByFilmId";
    //通过上映电影的id和电影院的cinemaId 查询正在热映的播放的电影信息
    public static final String findReleaseFilmFyCinemaIdAndFilmId="releaseFilm/findReleaseFilmFyCinemaIdAndFilmId";

    public static final String defaultHeadImg=HttpUrl.filmTicketUrl+"upload/timg.jpg";

    public static boolean isLogin=false;
    public static String useraccount="";
    public static String str="上海";
}


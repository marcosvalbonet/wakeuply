package com.valbonet.wakeuplyapp.data;

import com.valbonet.wakeuplyapp.model.search.Muser;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SWApiService {
    @FormUrlEncoded
    @POST("app/webservices/musers.php")
    Observable<List<Muser>> getMusersList(@Field("function") String function,
                                          @Field("offset") String offset,
                                          @Field("size") String size);

    @FormUrlEncoded
    @POST("app/webservices/musers.php")
    Observable<List<Muser>> findMusersByNickname(@Field("function") String function,
                                                 @Field("nickname") String nickname);


    @FormUrlEncoded
    @POST("app/webservices/musers.php")
    Call<Boolean> addNewTikTokMuser(@Field("function") String function,
                               @Field("nickname") String nickname,
                               @Field("name") String name,
                               @Field("userId") String userId,
                               @Field("secUid") String secUid,
                               @Field("imgSrc") String imgSrc);


    @FormUrlEncoded
    @POST("app/webservices/musers.php")
    Call<Boolean> updateUserId(@Field("function") String function,
                               @Field("nickname") String nickname,
                               @Field("userId") String userId);

    @FormUrlEncoded
    @POST("app/webservices/musers.php")
    Call<Boolean> updateSecUid(@Field("function") String function,
                               @Field("nickname") String nickname,
                               @Field("secUid") String secUid);

    @FormUrlEncoded
    @POST("app/webservices/musers.php")
    Call<Boolean> updateTiktokerImage(@Field("function") String function,
                                      @Field("nickname") String nickname,
                                      @Field("imgSrc") String imgSrc);

}

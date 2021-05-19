package com.valbonet.wakeuplyapp.data;

import com.valbonet.wakeuplyapp.model.Muser;
import com.valbonet.wakeuplyapp.model.Tiktok;
import com.valbonet.wakeuplyapp.model.Video;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface SWebService {

    //@Headers("Accept: application/json")
    @GET("node/share/user")
    Observable<Muser> getUserDetails();

    @GET("{nickuser}/video/{id}")
    Observable<String> getVideoData(@Path("nickuser") String nickuser, @Path("id") String id, @Header("Cookie")String cookie);

}

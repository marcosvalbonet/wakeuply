package com.valbonet.wakeuplyapp.data;

import com.valbonet.wakeuplyapp.model.Video;
import com.valbonet.wakeuplyapp.model.item.Tiktoker;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.QueryMap;

public interface SWService {

    //@Headers("Accept: application/json")
    @GET("item/detail/")
    Observable<Video> getItemDetails();

    @GET("post/item_list/")
    Observable<Tiktoker> getItemList(@QueryMap HashMap<String, String> params);

}

package com.valbonet.wakeuplyapp;

import android.util.Log;

import com.valbonet.wakeuplyapp.data.DataRepository;
import com.valbonet.wakeuplyapp.data.connection.Data;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;

public class RandomTest {
    @Test
    public void getsecUid() throws Exception {
        String resHTML = "\"createTime\":1451423399,\"verified\":false,\"secUid\":\"MS4wLjABAAAAyvBfAROOnIhVBZP9K9ISGAPB5GxL04rwxKCbTxqZ7SQ\",\"ftc\":false";

//            String substring = resHTML.substring(resHTML.indexOf("\"userInfo\":{\"user\":{\"id\":")+25);
//            String userId = substring.substring(1, substring.indexOf("\",\""));

        //String regex = "<script id=\"__NEXT_DATA__\" type=\"application/json\" crossorigin=\"anonymous\">(.*)</script><script crossorigin=\"anonymous\" nomodule=";
        String regex = "\"secUid\":\"(.*)\",\"";
        Pattern patron = Pattern.compile(regex);
        Matcher matcher = patron.matcher(resHTML);
        boolean value = matcher.find();

        String tacResult = matcher.group(1);

        assertEquals(tacResult, "MS4wLjABAAAAyvBfAROOnIhVBZP9K9ISGAPB5GxL04rwxKCbTxqZ7SQ");
        assert(value = true);
    }

    @Test
    public void updateSecUid() throws Exception {
        DataRepository repository = new DataRepository();

        Call<Boolean> call = repository.updateSecUid("@wakeuply", "test");

        call.enqueue(new Callback<Boolean>(){

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d("updateSecUid call",response.message());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


}

package com.example.pos_tagger;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitExService {

    String URL = "http://13.124.187.219:5000";

    @POST("/pos")
    Call<ResData> getData(@Body ReqData body);

}

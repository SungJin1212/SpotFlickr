package com.example.lee.spotflickr.retrofit;

import com.example.lee.spotflickr.retrofit.parser.PhotoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitService {

    @GET()
    Call<PhotoList> Search_Photo(@Url String url);


    @GET()
    Call<String> getStringResponse(@Url String url);

}
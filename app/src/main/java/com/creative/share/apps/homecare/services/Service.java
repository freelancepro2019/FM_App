package com.creative.share.apps.homecare.services;


import com.creative.share.apps.homecare.models.PlaceGeocodeData;
import com.creative.share.apps.homecare.models.PlaceMapDetailsData;
import com.creative.share.apps.homecare.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @Multipart
    @POST("api/provider-register")
    Call<UserModel> signUpDoctorWithImage(@Part("name") RequestBody name,
                                          @Part("phone") RequestBody phone,
                                          @Part("phone_code") RequestBody phone_code,
                                          @Part("password") RequestBody password,
                                          @Part("email") RequestBody email,
                                          @Part("gender") RequestBody gender,
                                          @Part("soft_type") RequestBody soft_type,
                                          @Part("department") RequestBody department,
                                          @Part("exper") RequestBody exper,
                                          @Part("about") RequestBody about,
                                          @Part MultipartBody.Part logo

    );

    @Multipart
    @POST("api/provider-register")
    Call<UserModel> signUpDoctorWithoutImage(@Part("name") RequestBody name,
                                             @Part("phone") RequestBody phone,
                                             @Part("phone_code") RequestBody phone_code,
                                             @Part("password") RequestBody password,
                                             @Part("email") RequestBody email,
                                             @Part("gender") RequestBody gender,
                                             @Part("soft_type") RequestBody soft_type,
                                             @Part("department") RequestBody department,
                                             @Part("exper") RequestBody exper,
                                             @Part("about") RequestBody about

    );


    @Multipart
    @POST("api/user-register")
    Call<UserModel> signUpClientWithImage(@Part("name") RequestBody name,
                                          @Part("phone") RequestBody phone,
                                          @Part("phone_code") RequestBody phone_code,
                                          @Part("password") RequestBody password,
                                          @Part("email") RequestBody email,
                                          @Part("gender") RequestBody gender,
                                          @Part("soft_type") RequestBody soft_type,
                                          @Part MultipartBody.Part logo

    );

    @Multipart
    @POST("api/user-register")
    Call<UserModel> signUpClientWithoutImage(@Part("name") RequestBody name,
                                             @Part("phone") RequestBody phone,
                                             @Part("phone_code") RequestBody phone_code,
                                             @Part("password") RequestBody password,
                                             @Part("email") RequestBody email,
                                             @Part("gender") RequestBody gender,
                                             @Part("soft_type") RequestBody soft_type

    );

    @FormUrlEncoded
    @POST("Api/login")
    Call<UserModel> login(@Field("phone") String phone,
                          @Field("phone_code") String phone_code,
                          @Field("password") String password);

    @GET("api/logout")
    Call<UserModel> logout(@Query("firebase_token") String firebase_token);
}



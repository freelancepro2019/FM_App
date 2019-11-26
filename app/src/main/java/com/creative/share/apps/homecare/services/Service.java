package com.creative.share.apps.homecare.services;


import com.creative.share.apps.homecare.models.NotificationDataModel;
import com.creative.share.apps.homecare.models.PlaceGeocodeData;
import com.creative.share.apps.homecare.models.PlaceMapDetailsData;
import com.creative.share.apps.homecare.models.ServicesDataModel;
import com.creative.share.apps.homecare.models.SubServicesModel;
import com.creative.share.apps.homecare.models.TermsDataModel;
import com.creative.share.apps.homecare.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
    Call<UserModel> signUpDoctorWithImage(@Header("device-lang") String header,
                                          @Part("name") RequestBody name,
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
    Call<UserModel> signUpDoctorWithoutImage(@Header("device-lang") String header,
                                             @Part("name") RequestBody name,
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
    Call<UserModel> signUpClientWithImage(@Header("device-lang") String header,
                                          @Part("name") RequestBody name,
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
    Call<UserModel> signUpClientWithoutImage(@Header("device-lang") String header,
                                             @Part("name") RequestBody name,
                                             @Part("phone") RequestBody phone,
                                             @Part("phone_code") RequestBody phone_code,
                                             @Part("password") RequestBody password,
                                             @Part("email") RequestBody email,
                                             @Part("gender") RequestBody gender,
                                             @Part("soft_type") RequestBody soft_type

    );

    @FormUrlEncoded
    @POST("Api/login")
    Call<UserModel> login(@Header("device-lang") String header,
                          @Field("phone") String phone,
                          @Field("phone_code") String phone_code,
                          @Field("password") String password);

    @GET("api/logout")
    Call<UserModel> logout(@Header("device-lang") String header,
                           @Query("firebase_token") String firebase_token);


    @GET("api/main-services")
    Call<ServicesDataModel> get_services(@Header("device-lang") String header);

    @GET("api/get-sub-services")
    Call<SubServicesModel> get_sub_services(@Header("device-lang") String header,
                                            @Query("main_service_id") String main_service_id);

    @FormUrlEncoded
    @POST("api/contact-us")
    Call<ResponseBody> contactUs(@Header("device-lang") String header,
                                 @Field("name") String name,
                                 @Field("email") String email,
                                 @Field("subject") String subject,
                                 @Field("message") String message
    );


    @GET("api/setting")
    Call<TermsDataModel> getAppData(@Header("device-lang") String header);


    @GET("api/my-notifications")
    Call<NotificationDataModel> getNotifications(@Header("device-lang") String header,
                                                 @Header("Authorization") String user_token,
                                                 @Query("page") int page,
                                                 @Query("limit_per_page") int limit_per_page
                                          );

}



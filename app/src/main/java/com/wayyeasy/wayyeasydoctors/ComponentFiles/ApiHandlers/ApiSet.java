package com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers;

import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.user_booked_response_model;
import com.wayyeasy.wayyeasydoctors.Models.prescription_response_model;
import com.wayyeasy.wayyeasydoctors.Models.single_user_response_model;
import com.wayyeasy.wayyeasydoctors.Models.verify_response_model;
import com.wayyeasy.wayyeasydoctors.Models.verify_response_model_sub;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiSet {
    @POST("physicians/Signup")
    Call<verify_response_model> register(@Body HashMap<String, String> map);

    @POST("physicians/login")
    Call<verify_response_model> login(@Body HashMap<String, String> map);

    @GET("physicians/getPhysiciansById/{physicianId}")
    Call<verify_response_model_sub> getPhysicianById(@Header("Authorization") String token,
                                                     @Path("physicianId") String physicianId);

    @PATCH("physicians/updateToken")
    Call<Void> updateToken(@Header("Authorization") String token,
                           @Body HashMap<String, String> fcmToken);

    @GET("user/fetchUserById/{userId}")
    Call<single_user_response_model> fetchUser(@Header("Authorization") String token,
                                               @Path("userId") String userId);

    @GET("physicianBookedByUsers/getUsersUnderPhysician")
    Call<user_booked_response_model> getPhysicianPaidUsers(@Header("Authorization") String token);

    @PATCH("physicianBookedByUsers/addUserPrescriptions/{userId}")
    Call<verify_response_model> addPrescription(@Header("Authorization") String token,
                                                @Path("userId") String userId,
                                                @Body List<prescription_response_model> data);

    @PATCH("physicianBookedByUsers/finishConsultancyByDoctor/{userId}")
    Call<verify_response_model> finishConsultation(@Header("Authorization") String token,
                                                   @Path("userId") String userId);
}

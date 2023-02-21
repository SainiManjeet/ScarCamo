package com.scarcamo.retrofit;

import com.scarcamo.pojo.AboutPojo;
import com.scarcamo.pojo.NotificationPojo;
import com.scarcamo.pojo.RegistrationPojo;
import com.scarcamo.pojo.ShadePojo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Ravinder on 2/24/2018.
 */

public interface ScarCamoServices {

    @GET("shades/all")
    Call<ShadePojo> getShades();

    @GET("products/all/{shade_id}")
    Call<ShadePojo> getProducts(@Path("shade_id") String shade_id);

    @GET("notifications/show/{UID}")
    Call<ShadePojo> getPNotification(@Path("UID") String id);

    @GET("orders/show/{uid}")
    Call<ShadePojo> getOrders(@Path("uid") String id);

    @GET("users/show/{uid}")
    Call<ShadePojo> getUserDetail(@Path("uid") String id);


    @FormUrlEncoded
    @HTTP(method = "DELETE",path = "notifications/delete",hasBody = true)
    Call<NotificationPojo> deleteNotification(@Field("id") String id);

    @GET("pages/aboutus")
    Call<AboutPojo> aboutUs();
    /*@FormUrlEncoded
    @POST("users/signup")
    Call<ShadePojo> createUser(@Field("username") String userName, @Field("password") String password,
    @Field("email") String email, @Field("deviceToken") String deviceToken, @Field("deviceType") String deviceType);
*/
    @FormUrlEncoded
    @POST("users/signup")
    Call<RegistrationPojo> createUser(@Field("username") String userName, @Field("password") String password, @Field("email") String email, @Field("deviceType") String deviceType, @Field("deviceToken") String deviceToken);

    @FormUrlEncoded
    @POST("users/login")
    Call<ShadePojo> loginUser(@Field("username") String userName, @Field("password") String password,@Field("deviceType") String deviceType, @Field("deviceToken") String deviceToken);

    @FormUrlEncoded
    @POST("users/changePassword")
    Call<ShadePojo> changePassword(@Field("uid") String userName, @Field("oldpassword") String oldpassword , @Field("newpassword") String newpassword);


    @FormUrlEncoded
    @POST("shades/find_shade")
    Call<ShadePojo> findShade(@Field("rgb") String rgb);

    @FormUrlEncoded
    @POST("users/scan_result")
    Call<ShadePojo> saveCard(@Field("uid") String uID, @Field("shade_id") String sID);

    @GET("users/show_scan_result/{uid}")
    Call<ShadePojo> getHistory(@Path("uid") String uID);

    @FormUrlEncoded
    @POST("users/forgetPassword")
    Call<ShadePojo> forgetPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("shipping_address/add")
    Call<ShadePojo> addAddress(@Field("uid") String uid, @Field("full_name") String full_name, @Field("mobile_number") String mobile_number,
                               @Field("pin_code") String pin_code,@Field("address_line1") String address_line1,
                               @Field("address_line2") String address_line2,@Field("city") String city,
                               @Field("state") String state);

    @FormUrlEncoded
    @POST("shipping_address/edit")
    Call<ShadePojo> editAddress(@Field("uid") String uid, @Field("full_name") String full_name, @Field("mobile_number") String mobile_number,
                                @Field("pin_code") String pin_code,@Field("address_line1") String address_line1,
                                @Field("address_line2") String address_line2,@Field("city") String city,
                                @Field("state") String state);

    @FormUrlEncoded
    @POST("payment/stripe")
    Call<ShadePojo> stripeToken(@Field("token") String token, @Field("uid") String uID,
                                @Field("amount") String amount,
                                @Field("pid") String pid,@Field("sid") String sid,
                                @Field("color_code") String color_code,@Field("shade_id") String shade_id);
}

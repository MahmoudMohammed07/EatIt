package com.android.eatit.Remote;

import com.android.eatit.Model.MyResponse;
import com.android.eatit.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAtv8ShW0:APA91bFkkLQQIJqs5ruBNwEJQZjwJ8PIs1YJKdPiVFxboKJAy-9WarfynHtj7Pgy4ychP3svgmvoYXD3G3XPBRqY_AHs9bSJDvycm2CB1jehwLGqgimj5gyX8nt4oxB1CqIeCtw9vXXQ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

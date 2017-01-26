package com.frostox.calculoII.apis;

import com.frostox.calculoII.entities.User;
import com.frostox.calculoII.entities.wrappers.ProductStatus;
import com.frostox.calculoII.entities.wrappers.Response;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by roger on 3/5/16.
 */
public interface PHP {

    @POST(value = "registerUser.php")
    Call<Response> registerUser(@Body User user);

    @POST(value = "verifyActivated.php")
    Call<ProductStatus> verifyActivated(@Body User user);

    @POST(value = "verifyKey.php")
    Call<Response> verifyKey(@Body User user);

}

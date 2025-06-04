package com.example.mybankapp.data.api

import com.example.mybankapp.data.model.Account
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AccountDetailsApi {

    @GET("accounts/{id}")
    fun getAccountDetails(@Path("id") id: String): Call<Account>

    @DELETE("accounts/{id}")
    fun deleteAccountDetails(@Path("id") id: String): Call<Unit>

    @PUT("accounts/{id}")
    fun updateAccountDetailsFully(
        @Path("id") id: String,
        @Body account: Account
    ): Call<Account>
}
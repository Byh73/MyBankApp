package com.example.mybankapp.data.api


import com.example.mybankapp.data.model.Account
import com.example.mybankapp.data.model.PatchAccountStatusDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Интерфейс для работы с API счетов
interface AccountApi {

    // Она отправляет GET-запрос на сервер по адресу accounts
    // и ожидает получить список аккаунтов (List<Account>).
    // Результат оборачивается в объект Call, чтобы им можно было управлять: запускать, отменять, обрабатывать ответ и ошибки.
    @GET("accounts")
    fun getAccounts(): Call<List<Account>>

    // Этот метод createAccount(...) отправляет данные об аккаунте на сервер в формате JSON,
    // по POST-запросу на accounts, и получает в ответ созданный аккаунт.
    @POST("accounts")
    fun createAccount(@Body account: com.example.mybankapp.data.model.Account): Call<Account>

    //Метод deleteAccount(id) отправляет DELETE-запрос на сервер для удаления аккаунта с заданным ID,
    // и в ответ получает пустой запрос.
    @DELETE("accounts/{id}")
    fun deleteAccount(@Path("id") id: String): Call<Unit>

    //@PUT("accounts/{id}")	Делает PUT-запрос по адресу /accounts/{id}
    //@Path("id")	Подставляет ID в адрес
    //@Body account	Отправляет новые данные аккаунта в теле запроса
    //Call<Account>	Возвращает обновлённый аккаунт от сервера
    @PUT("accounts/{id}")
    fun updateAccountFully(
        @Path("id") id: String,
        @Body account: Account
    ): Call<Account>

    //@PATCH("accounts/{id}") — отправляет PATCH-запрос на accounts/{id}.
    //PATCH — это тип HTTP-запроса, который используется для частичного обновления ресурса на сервере.
    //@Path("id") — подставляет id в URL.
    //@Body patchAccountStatusDTO — отправляет частичные данные (например, только поле status).
    //Call<Account> — в ответ возвращается обновлённый аккаунт.
    @PATCH("accounts/{id}")
    fun patchAccountStatus(
        @Path("id") id: String,
        @Body patchAccountStatusDTO: PatchAccountStatusDTO
    ): Call<Account>


}
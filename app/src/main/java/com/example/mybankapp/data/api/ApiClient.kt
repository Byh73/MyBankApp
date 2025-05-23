package com.example.mybankapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // успешный ответ с бэкенда: 200, 201, 202, 203, 204
    // обработанная ошибка на бэкенде: 400..
    // необработанная ошибка на бэкенде: 500...

    // Логирование HTTP-запросов и ответов для отладки
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // HTTP-клиент с подключенным логированием
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Инициализация Retrofit с базовым URL и конвертером JSON
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://683090fd6205ab0d6c399ab1.mockapi.io/api/accounts/")
        .client(httpClient) // 👈 подключаем клиент с логированием
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Экземпляр API для работы с счетами
    val accountApi: AccountApi = retrofit.create(AccountApi::class.java)
}
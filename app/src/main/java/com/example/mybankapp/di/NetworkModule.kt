package com.example.mybankapp.di

import com.example.mybankapp.data.api.AccountApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module //Помечает класс как модуль зависимостей для Hilt
@InstallIn(SingletonComponent::class) //Указывает, что зависимости живут во всём приложении
// (singleton) паттерн проектирования, гарантирующий, что у класса будет только один экземпляр

//"Вот объект NetworkModule, который предоставляет зависимости (например, Retrofit, OkHttp),
// и эти зависимости будут существовать в течение всей жизни приложения."
object NetworkModule {

    // успешный ответ с бэкенда: 200, 201, 202, 203, 204
    // обработанная ошибка на бэкенде: 400..
    // необработанная ошибка на бэкенде: 500...

    //Этот метод создаёт и предоставляет HttpLoggingInterceptor "логгер" для сетевых запросов и отдаёт его Hilt'у,
    //чтобы видеть в логах всё, что происходит в сети. Очень полезно при разработке и отладке.
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    //Он создаёт OkHttpClient (сетевой клиент)
    // и добавляет в него логирование запросов с помощью HttpLoggingInterceptor.
    //Затем этот клиент передаётся в другие части приложения через Hilt.
    // умеет отправлять запросы,
    //пишет всё в лог (для отладки),
    //может использоваться в Retrofit.
    @Provides
    fun provideHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    //Он создаёт объект Retrofit — это библиотека,
    // с помощью которой ты легко отправляешь HTTP-запросы
    // и получаешь ответы в виде Kotlin-объектов.
    // Retrofit сам делает сетевые запросы.
    //Преобразует JSON в Kotlin-объекты и обратно.
    //Ты просто вызываешь функции интерфейса ApiService — и всё работает.
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://681cb239f74de1d219ad795d.mockapi.io/api/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Он создаёт реализацию интерфейса AccountApi с помощью Retrofit,
    // чтобы ты мог удобно вызывать API-запросы как обычные функции.
    // После этого ты можешь внедрять AccountApi куда угодно (в ViewModel, репозиторий и т.д.)
    // и просто вызывать accountApi.getAccounts() — Retrofit сам отправит запрос и обработает ответ
    @Provides
    fun provideAccountApi(retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }
}
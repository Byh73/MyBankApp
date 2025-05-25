package com.example.mybankapp.data.model

 //для отправки/получения JSON с сервера через Retrofit,
 //для отображения данных в приложении,
 //для хранения в базе данных или памяти.
data class Account(
    val id: String? = null,
    val name: String,
    val balance: String,
    val currency: String,
    val isActive: Boolean
)
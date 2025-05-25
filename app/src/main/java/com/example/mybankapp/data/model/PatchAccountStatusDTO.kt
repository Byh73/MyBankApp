package com.example.mybankapp.data.model

//Это модель для частичного обновления аккаунта, конкретно — статуса активности (isActive).
data class PatchAccountStatusDTO (
    val isActive: Boolean
)
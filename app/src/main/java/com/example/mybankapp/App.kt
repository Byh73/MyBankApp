package com.example.mybankapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//@HiltAndroidApp в классе App включает работу Hilt во всём приложении.
//Без него внедрение зависимостей работать не будет.
@HiltAndroidApp
class App: Application()
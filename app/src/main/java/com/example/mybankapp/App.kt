package com.example.mybankapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel

//@HiltAndroidApp в классе App включает работу Hilt во всём приложении.
//Без него внедрение зависимостей работать не будет.

@HiltViewModel
class App: Application()
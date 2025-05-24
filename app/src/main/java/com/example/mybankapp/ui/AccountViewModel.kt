package com.example.mybankapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybankapp.data.api.AccountApi
import com.example.mybankapp.data.model.Account
import com.example.mybankapp.data.model.PatchAccountStatusDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountApi: AccountApi
): ViewModel(){

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Загрузка списка счетов
    fun loadAccounts() {
        // Выполняем асинхронный запрос к API для получения списка счетов
        accountApi.getAccounts().enqueue(object : Callback<List<Account>> {
            // Обработка успешного ответа
            override fun onResponse(call: Call<List<Account>>, response: Response<List<Account>>) {
                if (response.isSuccessful) {
                    _accounts.value = response.body() ?: emptyList()
                } else {
                    // Показываем сообщение об ошибке, если ответ не успешен
                    _errorMessage.value = "Ошибка загрузки"
                }
            }

            // Обработка ошибки сети или других исключений
            override fun onFailure(call: Call<List<Account>>, t: Throwable) {
                // Показываем сообщение об ошибке сети
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    // Добавление нового счета
    fun addAccount(name: String, balance: String, currency: String) {
        // Создаем объект Account с переданными данными
        val account = Account(name = name, balance = balance, currency = currency, isActive = true)

        // Выполняем асинхронный запрос к API для создания нового счета
        accountApi.createAccount(account).enqueue(object : Callback<Account> {
            // Обработка успешного ответа
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    // Показываем сообщение об успешном добавлении счета
                    _successMessage.value = "Аккаунт добавлен"
                    // Обновляем список счетов после добавления
                    loadAccounts()
                } else {
                    // Показываем сообщение об ошибке, если ответ не успешен
                    _errorMessage.value = "Ошибка добавления"
                }
            }

            // Обработка ошибки сети или других исключений
            override fun onFailure(call: Call<Account>, t: Throwable) {
                // Показываем сообщение об ошибке сети
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    fun deleteAccount(id: String) {
        accountApi.deleteAccount(id).enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Удалено"
                    loadAccounts()
                } else {
                    _errorMessage.value = "Ошибка удаления"
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }

        } )
    }

    fun updateAccountFully(accountId: String, account: Account) {
        accountApi.updateAccountFully(accountId, account).enqueue(object: Callback<Account>{
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Успешно обновлен счет"
                    loadAccounts()
                } else {
                    _errorMessage.value = "Ошибка обновления счета"
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }

        })
    }

    // функция для обновления статуса счета через PATCH, передается accountId и isActive, переопределяем onResponse
    // и onFailure и обрабатываем ответ через LiveData
    fun updateAccountStatus(accountId: String, isActive: Boolean) {
        accountApi.patchAccountStatus(accountId, PatchAccountStatusDTO(isActive)).enqueue(object: Callback<Account>{
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Успешно обновлен cтатус счета"
                    loadAccounts()
                } else {
                    _errorMessage.value = "Ошибка обновления статуса счета"
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }

        })
    }
}
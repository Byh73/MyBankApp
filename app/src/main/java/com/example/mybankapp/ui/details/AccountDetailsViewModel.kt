package com.example.mybankapp.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mybankapp.data.api.AccountApi
import com.example.mybankapp.data.api.AccountDetailsApi
import com.example.mybankapp.data.model.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val accountDetailsApi: AccountDetailsApi
) : ViewModel() {
    private val _account = MutableLiveData<Account>()
    val account: LiveData<Account> = _account

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun loadAccount(accountId: String) {
        accountDetailsApi.getAccountDetails(id= accountId).enqueue(object : Callback<Account> {
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    _account.value = response.body()
                } else {
                    _errorMessage.value = "Ошибка загрузки"
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    fun updateAccountDetailsFully(accountId: String, account: Account) {
        accountDetailsApi.updateAccountDetailsFully(accountId, account).enqueue(object : Callback<Account> {
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Успешно обновлен счет"
                    loadAccount(accountId)
                } else {
                    _errorMessage.value = "Ошибка обновления счета"
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }

        })
    }

    fun deleteAccountDetails(id: String) {
        accountDetailsApi.deleteAccountDetails(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Удалено"
                    loadAccount(id)
                } else {
                    _errorMessage.value = "Ошибка удаления"
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }

        })
    }
}
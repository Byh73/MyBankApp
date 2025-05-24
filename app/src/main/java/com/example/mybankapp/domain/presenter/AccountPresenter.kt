package com.example.mybankapp.domain.presenter

import com.example.mybankapp.data.api.ApiClient
import com.example.mybankapp.data.model.PatchAccountStatusDTO
import com.example.mybankapp.data.model.Account
import com.example.mybankapp.domain.contract.AccountContract
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Presenter для управления логикой работы со счетами
// Реализует интерфейс AccountContract.Presenter
class AccountPresenter(private val view: AccountContract.View) : AccountContract.Presenter {

    // Загрузка списка счетов
    override fun loadAccounts() {
        // Выполняем асинхронный запрос к API для получения списка счетов
        ApiClient.accountApi.getAccounts().enqueue(object : Callback<List<Account>> {
            // Обработка успешного ответа
            override fun onResponse(call: Call<List<Account>>, response: Response<List<Account>>) {
                if (response.isSuccessful) {
                    // Передаем список счетов во View для отображения
                    view.showAccounts(response.body() ?: emptyList())
                } else {
                    // Показываем сообщение об ошибке, если ответ не успешен
                    view.showError("Ошибка загрузки")
                }
            }

            // Обработка ошибки сети или других исключений
            override fun onFailure(call: Call<List<Account>>, t: Throwable) {
                // Показываем сообщение об ошибке сети
                view.showError("Ошибка сети: ${t.message}")
            }
        })
    }

    // Добавление нового счета
    override fun addAccount(name: String, balance: String, currency: String) {
        // Создаем объект Account с переданными данными
        val account = Account(name = name, balance = balance, currency = currency, isActive = true)

        // Выполняем асинхронный запрос к API для создания нового счета
        ApiClient.accountApi.createAccount(account).enqueue(object : Callback<Account> {
            // Обработка успешного ответа
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    // Показываем сообщение об успешном добавлении счета
                    view.showSuccess("Аккаунт добавлен")
                    // Обновляем список счетов после добавления
                    loadAccounts()
                } else {
                    // Показываем сообщение об ошибке, если ответ не успешен
                    view.showError("Ошибка добавления")
                }
            }

            // Обработка ошибки сети или других исключений
            override fun onFailure(call: Call<Account>, t: Throwable) {
                // Показываем сообщение об ошибке сети
                view.showError("Ошибка сети: ${t.message}")
            }
        })
    }

    override fun deleteAccount(accountId: String) {
        ApiClient.accountApi.deleteAccount(accountId).enqueue(object: Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    view.showSuccess("Удалено")
                    loadAccounts()
                } else {
                    view.showError("Ошибка удаления")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                view.showError("Ошибка сети: ${t.message}")
            }

        } )
    }

    override fun updateAccountFully(accountId: String, account: Account) {
        ApiClient.accountApi.updateAccountFully(accountId, account).enqueue(object: Callback<Account>{
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    view.showSuccess("Успешно обновлен счет")
                    loadAccounts()
                } else {
                    view.showError("Ошибка обновления счета")
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                view.showError("Ошибка сети: ${t.message}")
            }

        })
    }

    override fun updateAccountStatus(accountId: String, isActive: Boolean) {
        ApiClient.accountApi.patchAccountStatus(accountId,
            PatchAccountStatusDTO(isActive)
        ).enqueue(object: Callback<Account>{
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    view.showSuccess("Успешно обновлен cтатус счета")
                    loadAccounts()
                } else {
                    view.showError("Ошибка обновления статуса счета")
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                view.showError("Ошибка сети: ${t.message}")
            }

        })
    }
}
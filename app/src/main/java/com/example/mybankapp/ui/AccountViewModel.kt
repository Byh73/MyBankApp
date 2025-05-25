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

//Это ViewModel, которая управляет данными аккаунтов для экрана и общается с API.
//загружает данные аккаунтов,
//хранит сообщения об успехе и ошибках,
//даёт экрану (Activity/Fragment) доступ к этим данным через LiveData.
// AccountViewModel — это посредник между UI и API.
//Он управляет данными и сообщает об успехе/ошибках.
//LiveData позволяет UI автоматически обновляться, когда данные меняются.
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountApi: AccountApi
) : ViewModel() {

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    //accountApi.getAccounts() — вызывает запрос к серверу для получения списка аккаунтов.
    //.enqueue(...) — выполняет этот запрос асинхронно (в фоновом потоке).
    // Если ответ успешный (response.isSuccessful):
    //Данные из ответа (response.body()) сохраняются в _accounts.
    //Это обновит экран, потому что LiveData (accounts) изменилось.
    //При ошибке: Устанавливается сообщение об ошибке в _errorMessage,
    // чтобы UI мог показать, что что-то пошло не так.
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

            //onFailure(...) вызывается, если запрос к серверу не дошёл
            // или произошла ошибка на устройстве (например, нет Wi-Fi).
            //t.message — содержит текст ошибки (например, "Ошибка сети").
            //_errorMessage.value = ... — сохраняет сообщение об ошибке,
            // чтобы показать его пользователю на экране.
            override fun onFailure(call: Call<List<Account>>, t: Throwable) {
                // Показываем сообщение об ошибке сети
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    //Создаёт объект Account с переданными данными:
    //name, balance, currency,
    //isActive = true — по умолчанию счёт активен.
    //Отправляет запрос на сервер через accountApi.createAccount(account):
    //Используется enqueue, чтобы запрос выполнялся асинхронно (в фоне, не блокируя интерфейс).
    //Показывается сообщение: "Аккаунт добавлен".
    //Вызывается loadAccounts() — чтобы перезагрузить список аккаунтов и отобразить новый.
    // Если ответ сервера неуспешен:
    //Показывается сообщение об ошибке: "Ошибка добавления".
    // Если не удалось подключиться к серверу (onFailure):
    //Показывается сообщение "Ошибка сети:".
    fun addAccount(name: String, balance: String, currency: String) {
        val account = Account(name = name, balance = balance, currency = currency, isActive = true)

        accountApi.createAccount(account).enqueue(object : Callback<Account> {
            override fun onResponse(call: Call<Account>, response: Response<Account>) {
                if (response.isSuccessful) {
                    _successMessage.value = "Аккаунт добавлен"
                    loadAccounts()
                } else {
                    _errorMessage.value = "Ошибка добавления"
                }
            }

            override fun onFailure(call: Call<Account>, t: Throwable) {
                _errorMessage.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    //Вызывает API-запрос на удаление:
    //accountApi.deleteAccount(id) — говорит серверу: "удали счёт с таким ID".
    //Асинхронно обрабатывает результат через .enqueue(...) — чтобы не блокировать интерфейс.
    // Если всё ок — показывает "Удалено" и обновляет список.
    //Если ошибка — показывает, что что-то пошло не так.
    fun deleteAccount(id: String) {
        accountApi.deleteAccount(id).enqueue(object : Callback<Unit> {
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

        })
    }

    //Эта функция полностью обновляет информацию о счёте на сервере по его id.
    // Принимает:
    //accountId — ID счёта, который нужно обновить.
    //account — новые данные счёта (имя, баланс, валюта, активность и т.д.).
    // Отправляет PUT-запрос на сервер:
    //accountApi.updateAccountFully(...) — заменяет все поля счёта на новые.
    fun updateAccountFully(accountId: String, account: Account) {
        accountApi.updateAccountFully(accountId, account).enqueue(object : Callback<Account> {
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

    //Она обновляет только статус (isActive) счёта на сервере с помощью запроса PATCH.
    //accountId — ID счёта, у которого нужно изменить статус.
    //isActive — новое значение статуса: true (активен) или false (неактивен).
    // Создаёт объект PatchAccountStatusDTO(isActive) — это "обёртка" для передачи только одного поля.
    // Отправляет PATCH-запрос через API:
    //patchAccountStatus(...) — говорит серверу изменить только isActive.
    fun updateAccountStatus(accountId: String, isActive: Boolean) {
        accountApi.patchAccountStatus(accountId, PatchAccountStatusDTO(isActive))
            .enqueue(object : Callback<Account> {
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
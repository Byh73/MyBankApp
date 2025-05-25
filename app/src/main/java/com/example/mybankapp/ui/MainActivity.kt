package com.example.mybankapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybankapp.R
import com.example.mybankapp.data.model.Account
import com.example.mybankapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


//Аннотация @AndroidEntryPoint говорит Hilt-у:
//"Можно внедрять зависимости (например, ViewModel) в эту Activity".
//Без неё Hilt не сможет "вставить" ViewModel, репозитории и т.д.
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //Это используется для работы с View Binding
    // (привязка к элементам макета activity_main.xml).
    //binding помогает удобно обращаться к кнопкам, текстам и т.д.
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //Это подключение ViewModel для управления логикой работы со счетами.
    //by viewModels() — безопасный способ получить ViewModel в Activity
    //(автоматически привязывается к жизненному циклу).
    private val viewModel: AccountViewModel by viewModels()

    //Это объявление адаптера для RecyclerView,списка.
    private lateinit var adapter: AccountAdapter

    //Это метод, который запускается при старте MainActivity.
    // Создаётся привязка (binding) к макету activity_main.xml.
    //Отображается экран с помощью setContentView.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Создаётся AccountAdapter, который показывает список счетов.
        // Обрабатываются действия:
        //Удаление счета
        //Редактирование счета
        //Изменение статуса (активен/неактивен)
        adapter = AccountAdapter(
            onDelete = { id ->
                viewModel.deleteAccount(id)
            },
            onEdit = { account ->
                //show edit dialog
                showEditDialog(account)
            },
            onStatusToggle = { id, isChecked ->
                viewModel.updateAccountStatus(id, isChecked)
            }
        )
        //Говорим RecyclerView, как размещать элементы (по вертикали).
        //Подключаем адаптер для показа списка.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        //При нажатии на кнопку вызывается окно для добавления нового счёта.
        binding.btnAdd.setOnClickListener {
            showAddDialog()
        }

        //Загружаем список счетов с сервера.
        //Подписываемся на LiveData, чтобы получать обновления и отображать их в UI.
        viewModel.loadAccounts()
        subscribeToLiveData()
    }

    //Функция следит за изменениями данных и сообщений в ViewModel,
    //и автоматически обновляет интерфейс (список счетов и уведомления).
    private fun subscribeToLiveData() {
        // Подписываюсь через observe к Livedata переменной accounts из viewModel
        // и обновляю адаптер
        viewModel.accounts.observe(this) {
            adapter.submitList(it)
        }
        viewModel.successMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    //Загружается макет окна dialog_add_account.xml, где есть поля для ввода.
    // Эти строки получают доступ к полям, в которые пользователь будет вводить данные.
    // Создаётся диалог с заголовком и формой, которую мы только что подготовили.
    //Когда пользователь нажимает "Добавить":Считываются данные из полей.
    //Вызывается метод addAccount() во ViewModel, чтобы создать новый счёт.
    //При нажатии "Отмена" окно просто закрывается.
    private fun showAddDialog() {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_account, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.etName)
        val balanceInput = dialogView.findViewById<EditText>(R.id.etBalance)
        val currencyInput = dialogView.findViewById<EditText>(R.id.etCurrency)

        AlertDialog.Builder(this)
            .setTitle("Добавить счёт") // Заголовок диалога
            .setView(dialogView) // Установка пользовательского макета
            .setPositiveButton("Добавить") { _, _ ->

                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString()
                val currency = currencyInput.text.toString()

                viewModel.addAccount(name, balance, currency)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // Создаётся макет окна редактирования:
    // Загружается разметка dialog_add_account.xml — та же, что и при добавлении счёта.
    // Пользователь сразу видит данные выбранного счёта и может изменить их.
    //Диалог с заголовком "Редактировать счёт".
    //Считываем новые значения из полей.
    //Создаём копию старого счёта с новыми значениями:
    // Отправляем изменения через viewModel.updateAccountFully(...) — это отправка PUT-запроса на сервер.
    //В конце просто закрывает диалог без изменений.
    private fun showEditDialog(account: Account) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_account, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.etName)
        val balanceInput = dialogView.findViewById<EditText>(R.id.etBalance)
        val currencyInput = dialogView.findViewById<EditText>(R.id.etCurrency)

        // Заполняем текущими данными
        nameInput.setText(account.name)
        balanceInput.setText(account.balance)
        currencyInput.setText(account.currency)

        AlertDialog.Builder(this)
            .setTitle("Редактировать счёт")
            .setView(dialogView)
            .setPositiveButton("Обновить") { _, _ ->
                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString()
                val currency = currencyInput.text.toString()

                val updated = account.copy(
                    name = name,
                    balance = balance,
                    currency = currency
                )

                viewModel.updateAccountFully(updated.id!!, updated)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

}
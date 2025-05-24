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


@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // Презентер для управления логикой работы со счетами
    private val viewModel: AccountViewModel by viewModels()

    private lateinit var adapter: AccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnAdd.setOnClickListener {
            showAddDialog()
        }

        // Загрузка списка счетов
        viewModel.loadAccounts()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData(){
        // Подписываюясь через observe к Livedata переменной accounts из viewModel
        // и обновляю адаптер
        viewModel.accounts.observe(this){
            adapter.submitList(it)
        }
        viewModel.successMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.errorMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    // Показ диалогового окна для добавления нового счета
    private fun showAddDialog() {
        // Создание и настройка пользовательского макета диалога
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_account, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.etName)
        val balanceInput = dialogView.findViewById<EditText>(R.id.etBalance)
        val currencyInput = dialogView.findViewById<EditText>(R.id.etCurrency)

        // Создание и отображение диалогового окна
        AlertDialog.Builder(this)
            .setTitle("Добавить счёт") // Заголовок диалога
            .setView(dialogView) // Установка пользовательского макета
            .setPositiveButton("Добавить") { _, _ ->
                // Получение данных из полей ввода
                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString()
                val currency = currencyInput.text.toString()

                // Вызов метода добавления счета в презентере
                viewModel.addAccount(name, balance, currency)
            }
            .setNegativeButton("Отмена", null) // Кнопка отмены
            .show()
    }

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
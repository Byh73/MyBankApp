package com.example.mybankapp.ui.details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mybankapp.R
import com.example.mybankapp.data.model.Account
import com.example.mybankapp.databinding.ActivityDetailsBinding
import com.example.mybankapp.ui.AccountViewModel


class DetailsActivity : AppCompatActivity() {

    private var _binding: ActivityDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountDetailsViewModel by viewModels()

    private val accountId: String by lazy { intent.getStringExtra("account_id") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initClicks()
        subscribeToLiveData()

        Toast.makeText(this, "ID счета: $accountId", Toast.LENGTH_SHORT).show()

    }

    private fun initClicks() {
        with(binding) {
            btnEdit.setOnClickListener {
                showEditDialog(viewModel.account.value)
            }
            btnDelete.setOnClickListener {
                viewModel.deleteAccountDetails(id = viewModel.account.value!!.id!!)
            }
        }
    }

    private fun subscribeToLiveData() {
        viewModel.account.observe(this@DetailsActivity) { data ->
            setAccountInfo(
                data
            )
        }
    }

    private fun setAccountInfo(account: Account?) {
        with(binding) {
            account?.let {
                tvName.text = "Название: ${it.name}"
                tvIsActive.text = "Статус активности: ${it.isActive.toString()}"
                tvBalance.text = "Баланс: ${it.balance}"
                tvCurrency.text = "Валюта: ${it.currency}"
            }
        }
    }

    private fun showEditDialog(account: Account?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_account, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.etName)
        val balanceInput = dialogView.findViewById<EditText>(R.id.etBalance)
        val currencyInput = dialogView.findViewById<EditText>(R.id.etCurrency)

        nameInput.setText(account?.name)
        balanceInput.setText(account?.balance)
        currencyInput.setText(account?.currency)

        AlertDialog.Builder(this)
            .setTitle("Редактировать счёт")
            .setView(dialogView)
            .setPositiveButton("Обновить") { _, _ ->
                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString()
                val currency = currencyInput.text.toString()

                val updatedAccount = account?.copy(
                    name = name,
                    balance = balance,
                    currency = currency
                )

                viewModel.updateAccountDetailsFully(accountId, updatedAccount!!)

            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
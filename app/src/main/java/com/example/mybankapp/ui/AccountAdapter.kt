package com.example.mybankapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mybankapp.R
import com.example.mybankapp.data.model.Account

class AccountAdapter : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    private val items = mutableListOf<Account>()

    // Метод для обновления списка данных в адаптере
    fun submitList(data: List<Account>) {
        items.clear() // Очищаем текущий список
        items.addAll(data) // Добавляем новые данные
        notifyDataSetChanged() // Уведомляем RecyclerView об изменениях
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class AccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(account: Account) = with(itemView) {
            findViewById<TextView>(R.id.tvName).text = account.name
            findViewById<TextView>(R.id.tvBalance).text = "${account.balance} ${account.currency}"
        }
    }
}
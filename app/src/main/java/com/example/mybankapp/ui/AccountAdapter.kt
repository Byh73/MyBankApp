package com.example.mybankapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mybankapp.R
import com.example.mybankapp.data.model.Account

class AccountAdapter (
    val onStatusToggle: (String, Boolean) -> Unit,
    val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    private val items = mutableListOf<Account>()

    fun submitList(data: List<Account>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
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

            val switchActive = findViewById<SwitchCompat>(R.id.switchActive)
            switchActive.setOnCheckedChangeListener(null)
            switchActive.isChecked = account.isActive
            switchActive.setOnCheckedChangeListener { buttonView, isChecked ->
                account.id?.let { onStatusToggle(it, isChecked) }
            }

            setOnClickListener { account.id?.let { onItemClick(it) } }

        }
    }
}
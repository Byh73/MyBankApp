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

//Это адаптер для RecyclerView, который отображает список аккаунтов и умеет:
// Удалять аккаунт (onDelete)
// Редактировать аккаунт (onEdit)
// Включать/выключать статус (onStatusToggle)
class AccountAdapter(
    val onDelete: (String) -> Unit,
    val onEdit: (Account) -> Unit,
    val onStatusToggle: (String, Boolean) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    //Это приватный список, который хранит коллекцию объектов типа Account.
    private val items = mutableListOf<Account>()

    // Метод для обновления списка данных в адаптере
    fun submitList(data: List<Account>) {
        items.clear() // Очищаем текущий список
        items.addAll(data) // Добавляем новые данные
        notifyDataSetChanged() // Уведомляем RecyclerView об изменениях
    }//Нужно чтобы, когда у тебя появились новые данные например с сервера,
    // адаптер показал их пользователю.

    //Создаёт View - загружает макет item_account.xml, который описывает, как выглядит один элемент списка.
    //Оборачивает этот View в AccountViewHolder - объект, который управляет этим элементом списка.
    //Возвращает новый ViewHolder - чтобы RecyclerView мог его использовать для отображения данных.
    // RecyclerView берёт этот метод, чтобы создавать столько элементов списка,
    // сколько нужно, и переиспользовать их при прокрутке.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    //items[position] — получает аккаунт из списка по номеру позиции.
    //holder.bind(...) — вызывает функцию в AccountViewHolder,
    // которая обновляет содержимое элемента (например, имя, баланс, статус).
    //RecyclerView использует этот метод, чтобы обновить вид каждого элемента списка,
    // когда он появляется на экране.
    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(items[position])
    }

    //Этот метод сообщает RecyclerView, сколько элементов нужно показать в списке.
    //То есть, сколько у нас аккаунтов в списке items.
    override fun getItemCount(): Int = items.size

    //Это ViewHolder, который управляет одной карточкой (одним элементом в списке).
    //bind(account) — функция, которая получает данные об одном аккаунте
    // и показывает их в элементе списка. Дальше имя и баланс акаунта.
    inner class AccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(account: Account) = with(itemView) {
            findViewById<TextView>(R.id.tvName).text = account.name
            findViewById<TextView>(R.id.tvBalance).text = "${account.balance} ${account.currency}"

            //При нажатии вызывает функцию из адаптера, чтобы удалить аккаунт.
            val btnDelete = findViewById<Button>(R.id.btnDelete)
            btnDelete.setOnClickListener {
                account.id?.let { onDelete(it) }
            }

            //А этот чтобы, например, открыть экран редактирования.
            val btnEdit = findViewById<Button>(R.id.btnEdit)
            btnEdit.setOnClickListener {
                onEdit(account)
            }

            //Сначала убираем слушателя, чтобы не срабатывал при установке значения.
            //Устанавливаем, включён ли аккаунт.
            //Назначаем слушатель, чтобы при переключении вызывать onStatusToggle(id, true/false).
            val switchActive = findViewById<SwitchCompat>(R.id.switchActive)
            switchActive.setOnCheckedChangeListener(null) // убираем старый слушатель
            switchActive.isChecked = account.isActive     // показываем актуальное значение
            switchActive.setOnCheckedChangeListener { buttonView, isChecked ->
                account.id?.let { onStatusToggle(it, isChecked) }
            }


        }
    }
}
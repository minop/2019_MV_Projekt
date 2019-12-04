package com.example.messagingappmv.screens.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.UserMessages
import com.example.messagingappmv.databinding.ListItemChatContactBinding

import com.example.messagingappmv.databinding.ListItemChatUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatAdapter(val clickListener: ChatListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(ChatDiffCallback()) {
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
    private val adapterScope = CoroutineScope(Dispatchers.Default)


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.UserMessage
                holder.bind(nightItem.userMessage, clickListener)
            }
            is ViewHolder2 -> {
                val nightItem = getItem(position) as DataItem.UserMessage
                holder.bind(nightItem.userMessage, clickListener)
            }
        }
//        holder.bind(getItem(position)!!, clickListener)
//        val current_message = getItem(position!!)
//        if(current_message.uid == 1L){
//            holder.itemView.user_name_string.setBackgroundColor(Color.parseColor("#000000"))
//        }
//        holder.itemView.user_name_string.setBackgroundColor(Color.parseColor("#000000"))
//        holder.itemView.user_name_string.setBackgroundColor(R.drawable.oval_for_one_message)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> ViewHolder2.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    fun addHeaderAndSubmitList(list: List<UserMessages>?) {
        adapterScope.launch {
//            val items = when (list) {
//                null -> listOf(DataItem.ContactMessage)
//                else -> listOf(DataItem.ContactMessage) + list.map { DataItem.UserMessage(it) }
//            }
            val items = list?.map { DataItem.UserMessage(it) }


            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is DataItem.ContactMessage -> ITEM_VIEW_TYPE_HEADER
//            is DataItem.UserMessage -> ITEM_VIEW_TYPE_ITEM
//        }
        if(getItem(position).uid == 8L){
            return ITEM_VIEW_TYPE_ITEM
        } else {
            return ITEM_VIEW_TYPE_HEADER
        }
    }

    class ViewHolder private constructor(val binding: ListItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserMessages, clickListener: ChatListener) {
            binding.chat = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemChatUserBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class ViewHolder2 private constructor(val binding: ListItemChatContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserMessages, clickListener: ChatListener) {
            binding.chat = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder2 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemChatContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolder2(binding)
            }
        }
    }

//    class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        companion object {
//            fun from(parent: ViewGroup): TextViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val view = layoutInflater.inflate(R.layout.list_item_chat_contact, parent, false)
//                return TextViewHolder(view)
//            }
//        }
//    }
}


class ChatDiffCallback : DiffUtil.ItemCallback<DataItem>() {

    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


class ChatListener(val clickListener: (userId: Long) -> Unit) {
    fun onClick(userMessages: UserMessages) = clickListener(userMessages.id)
}

sealed class DataItem {
    abstract val id: Long
    abstract val uid: Long

    data class UserMessage(val userMessage: UserMessages) : DataItem() {
        override val id = userMessage.id
        override val uid = userMessage.uid
    }

    data class ContactMessage(val userMessage: UserMessages): DataItem() {
        override val id = userMessage.id
        override val uid = userMessage.uid
    }
}


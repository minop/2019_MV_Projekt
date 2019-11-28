package com.example.messagingappmv.screens.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.UserContact
import com.example.messagingappmv.database.UserMessages
import com.example.messagingappmv.databinding.ListItemChatBinding
import com.example.messagingappmv.databinding.ListItemUserContactBinding

class ChatAdapter(val clickListener: ChatListener) : ListAdapter<UserMessages, ChatAdapter.ViewHolder>(ChatDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemChatBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: UserMessages, clickListener: ChatListener) {
            binding.chat = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemChatBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class ChatDiffCallback : DiffUtil.ItemCallback<UserMessages>() {

    override fun areItemsTheSame(oldItem: UserMessages, newItem: UserMessages): Boolean {
        return oldItem.user_id == newItem.user_id
    }

    override fun areContentsTheSame(oldItem: UserMessages, newItem: UserMessages): Boolean {
        return oldItem == newItem
    }
}


class ChatListener(val clickListener: (userId: Long) -> Unit) {
    fun onClick(userMessages: UserMessages) = clickListener(userMessages.user_id)
}

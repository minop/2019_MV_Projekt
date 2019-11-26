package com.example.messagingappmv.screens.contact_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.UserContact
import com.example.messagingappmv.databinding.ListItemUserContactBinding

class ContactListAdapter(val clickListener: ContactListListener) : ListAdapter<UserContact, ContactListAdapter.ViewHolder>(ContactListDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemUserContactBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: UserContact, clickListener: ContactListListener) {
            binding.userContact = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemUserContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class ContactListDiffCallback : DiffUtil.ItemCallback<UserContact>() {

    override fun areItemsTheSame(oldItem: UserContact, newItem: UserContact): Boolean {
        return oldItem.user_id == newItem.user_id
    }

    override fun areContentsTheSame(oldItem: UserContact, newItem: UserContact): Boolean {
        return oldItem == newItem
    }
}


class ContactListListener(val clickListener: (userId: Long) -> Unit) {
    fun onClick(userContact: UserContact) = clickListener(userContact.user_id)
}

package com.example.messagingappmv.screens.room_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.RoomContact
import com.example.messagingappmv.databinding.ListItemRoomContactBinding

class RoomListAdapter(val clickListener: RoomListListener) : ListAdapter<RoomContact, RoomListAdapter.ViewHolder>(RoomListDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemRoomContactBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: RoomContact, clickListener: RoomListListener) {
            binding.roomContact = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class RoomListDiffCallback : DiffUtil.ItemCallback<RoomContact>() {

    override fun areItemsTheSame(oldItem: RoomContact, newItem: RoomContact): Boolean {
        return oldItem.ssid == newItem.ssid
    }

    override fun areContentsTheSame(oldItem: RoomContact, newItem: RoomContact): Boolean {
        return oldItem == newItem
    }
}


class RoomListListener(val clickListener: (id: String) -> Unit) {
    fun onClick(RoomContact: RoomContact) = clickListener(RoomContact.ssid)
}

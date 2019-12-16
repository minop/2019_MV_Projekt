package com.example.messagingappmv.screens.room_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.RoomContact
import com.example.messagingappmv.databinding.ListItemRoomContactBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomListAdapter(val clickListener: RoomListListener, private val currentWifi: RoomContact
):
    ListAdapter<RoomContactDataItem, RecyclerView.ViewHolder>(RoomListDiffCallback()) {
    private val VIEW_ORDINARY = 0
    private val VIEW_CURRENT_WIFI = 1

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderOrdinaryRoom -> {
                val roomContactItem = getItem(position) as RoomContactDataItem
                holder.bind(roomContactItem.roomContact, clickListener)
            }
            is ViewHolderCurrentWifiRoom -> {
                holder.bind(currentWifi, clickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == this.itemCount) {
            VIEW_CURRENT_WIFI
        } else {
            VIEW_ORDINARY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_ORDINARY -> ViewHolderOrdinaryRoom.from(parent)
            VIEW_CURRENT_WIFI -> ViewHolderCurrentWifiRoom.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    fun addHeaderAndSubmitList(list: List<RoomContact>?) {
        adapterScope.launch {
            val items = list?.map { RoomContactDataItem(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class ViewHolderOrdinaryRoom private constructor(val binding: ListItemRoomContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RoomContact, clickListener: RoomListListener) {
            binding.roomContact = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderOrdinaryRoom {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolderOrdinaryRoom(binding)
            }
        }
    }

    class ViewHolderCurrentWifiRoom private constructor(val binding: ListItemRoomContactBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: RoomContact, clickListener: RoomListListener) {
            binding.roomContact = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderCurrentWifiRoom {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolderCurrentWifiRoom(binding)
            }
        }
    }
}


class RoomListDiffCallback : DiffUtil.ItemCallback<RoomContactDataItem>() {

    override fun areItemsTheSame(oldItem: RoomContactDataItem, newItem: RoomContactDataItem): Boolean {
        return oldItem.ssid == newItem.ssid
    }

    override fun areContentsTheSame(oldItem: RoomContactDataItem, newItem: RoomContactDataItem): Boolean {
        return oldItem == newItem
    }
}


class RoomListListener(val clickListener: (id: String) -> Unit) {
    fun onClick(RoomContact: RoomContact) = clickListener(RoomContact.ssid)
}


data class RoomContactDataItem(val roomContact: RoomContact) {
    val ssid = roomContact.ssid
}
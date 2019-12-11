package com.example.messagingappmv.screens.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.UserPosts
import com.example.messagingappmv.databinding.ListItemRoomContactBinding

import com.example.messagingappmv.databinding.ListItemRoomUserBinding
import com.example.messagingappmv.databinding.ListItemRoomUsersContactBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoomAdapter(val clickListener: RoomListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(RoomDiffCallback()) {
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
    private val adapterScope = CoroutineScope(Dispatchers.Default)


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.UserPost
                holder.bind(nightItem.userPost, clickListener)
            }
            is ViewHolder2 -> {
                val nightItem = getItem(position) as DataItem.UserPost
                holder.bind(nightItem.userPost, clickListener)
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

    fun addHeaderAndSubmitList(list: List<UserPosts>?) {
        adapterScope.launch {
//            val items = when (list) {
//                null -> listOf(DataItem.ContactMessage)
//                else -> listOf(DataItem.ContactMessage) + list.map { DataItem.UserPost(it) }
//            }
            val items = list?.map { DataItem.UserPost(it) }


            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is DataItem.ContactMessage -> ITEM_VIEW_TYPE_HEADER
//            is DataItem.UserPost -> ITEM_VIEW_TYPE_ITEM
//        }
        if(getItem(position).uid == 8L){
            return ITEM_VIEW_TYPE_ITEM
        } else {
            return ITEM_VIEW_TYPE_HEADER
        }
    }

    class ViewHolder private constructor(val binding: ListItemRoomUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserPosts, clickListener: RoomListener) {
            binding.room = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomUserBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class ViewHolder2 private constructor(val binding: ListItemRoomUsersContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserPosts, clickListener: RoomListener) {
            binding.room = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder2 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomUsersContactBinding.inflate(layoutInflater, parent, false)
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


class RoomDiffCallback : DiffUtil.ItemCallback<DataItem>() {

    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}


class RoomListener(val clickListener: (userId: Long) -> Unit) {
    fun onClick(userMessages: UserPosts) = clickListener(userMessages.id)
}

sealed class DataItem {
    abstract val id: Long
    abstract val uid: Long

    data class UserPost(val userPost: UserPosts) : DataItem() {
        override val id = userPost.id
        override val uid = userPost.uid
    }

    data class ContactMessage(val userPost: UserPosts): DataItem() {
        override val id = userPost.id
        override val uid = userPost.uid
    }
}


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

class RoomAdapter(    
    private val uid: Long,
    private val contactUid: Long,
    val clickListener: RoomListener
) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(RoomDiffCallback()) {
    private val ITEM_VIEW_TYPE_TEXT_CONTACT = 0
    private val ITEM_VIEW_TYPE_TEXT_USER = 1
    private val ITEM_VIEW_TYPE_IMAGE_CONTACT = 2
    private val ITEM_VIEW_TYPE_IMAGE_USER = 3
    
    private val adapterScope = CoroutineScope(Dispatchers.Default)
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderTextUser -> {
                val userPostItem = getItem(position) as DataItem.UserPost
                holder.bind(userPostItem.userPost, clickListener)
            }
            is ViewHolderTextContact -> {
                val userPostItem = getItem(position) as DataItem.UserPost
                holder.bind(userPostItem.userPost, clickListener)
            }
            is ViewHolderImgContact -> {
                val userPostItem = getItem(position) as DataItem.UserPost
                holder.bind(userPostItem.userPost)
            }
            is ViewHolderImgUser -> {
                val userPostItem = getItem(position) as DataItem.UserPost
                holder.bind(userPostItem.userPost)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_TEXT_CONTACT -> ViewHolderTextContact.from(parent)
            ITEM_VIEW_TYPE_TEXT_USER -> ViewHolderTextUser.from(parent)
            ITEM_VIEW_TYPE_IMAGE_CONTACT -> ViewHolderImgUser.from(parent)
            ITEM_VIEW_TYPE_IMAGE_USER -> ViewHolderImgContact.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    fun addHeaderAndSubmitList(list: List<UserPosts>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.UserPost(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
    
    override fun getItemViewType(position: Int): Int {
        val post = getItem(position).post
        if (post != "") {
            if (post.length >= 5) {
                if (post.substring(0, 4) == "gif:") {
                    return if (getItem(position).uid == uid) {
                        ITEM_VIEW_TYPE_IMAGE_USER
                    } else {
                        ITEM_VIEW_TYPE_IMAGE_CONTACT
                    }
                }
            }
            return if (getItem(position).uid == uid) {
                ITEM_VIEW_TYPE_TEXT_USER
            } else {
                ITEM_VIEW_TYPE_TEXT_CONTACT
            }

        } else return -1
    }

    class ViewHolderTextUser private constructor(val binding: ListItemRoomUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserPosts, clickListener: RoomListener) {
            binding.room = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderTextUser {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomUserBinding.inflate(layoutInflater, parent, false)
                return ViewHolderTextUser(binding)
            }
        }
    }

    class ViewHolderTextContact private constructor(val binding: ListItemRoomUsersContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserPosts, clickListener: RoomListener) {
            binding.room = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderTextContact {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomUsersContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolderTextContact(binding)
            }
        }
    }

    class ViewHolderImgContact private constructor(val binding: ImageItemRoomContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserPosts) {
            binding.room = item
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderImgContact {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageItemRoomContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolderImgContact(binding)
            }
        }
    }

    class ViewHolderImgUser private constructor(val binding: ImageItemRoomUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserPosts) {
            binding.room = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderImgUser {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageItemRoomUserBinding.inflate(layoutInflater, parent, false)
                return ViewHolderImgUser(binding)
            }
        }
    }
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
    abstract val post: String

    data class UserPost(val userPost: UserPosts) : DataItem() {
        override val id = userPost.id
        override val uid = userPost.uid
        override val post = userPost.post
    }

    data class ContactPost(val userPost: UserPosts) : DataItem() {
        override val id = userPost.id
        override val uid = userPost.uid
        override val post = userPost.post
    }
}


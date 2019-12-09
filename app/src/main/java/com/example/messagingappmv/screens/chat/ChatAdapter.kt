package com.example.messagingappmv.screens.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.UserMessages
import com.example.messagingappmv.databinding.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatAdapter(
    private val uid: Long,
    private val contactUid: Long,
    val clickListener: ChatListener
) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(ChatDiffCallback()) {
    private val ITEM_VIEW_TYPE_TEXT_CONTACT = 0
    private val ITEM_VIEW_TYPE_TEXT_USER = 1
    private val ITEM_VIEW_TYPE_IMAGE_CONTACT = 2
    private val ITEM_VIEW_TYPE_IMAGE_USER = 3

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderTextUser -> {
                val userMessageItem = getItem(position) as DataItem.UserMessage
                holder.bind(userMessageItem.userMessage, clickListener)
            }
            is ViewHolderTextContact -> {
                val userMessageItem = getItem(position) as DataItem.UserMessage
                holder.bind(userMessageItem.userMessage, clickListener)
            }
            is ViewHolderImgContact -> {
                val userMessageItem = getItem(position) as DataItem.UserMessage
                holder.bind(userMessageItem.userMessage)
            }
            is ViewHolderImgUser -> {
                val userMessageItem = getItem(position) as DataItem.UserMessage
                holder.bind(userMessageItem.userMessage)
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

    fun addHeaderAndSubmitList(list: List<UserMessages>?) {
        adapterScope.launch {
            val items = list?.map { DataItem.UserMessage(it) }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position).message
        if (message != "") {
            if (message.length >= 5) {
                if (message.substring(0, 4) == "gif:") {
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

    class ViewHolderTextUser private constructor(val binding: ListItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserMessages, clickListener: ChatListener) {
            binding.chat = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderTextUser {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemChatUserBinding.inflate(layoutInflater, parent, false)
                return ViewHolderTextUser(binding)
            }
        }
    }

    class ViewHolderTextContact private constructor(val binding: ListItemChatContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserMessages, clickListener: ChatListener) {
            binding.chat = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderTextContact {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemChatContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolderTextContact(binding)
            }
        }
    }

    class ViewHolderImgContact private constructor(val binding: ImageItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserMessages) {
            binding.chat = item
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderImgContact {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageItemContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolderImgContact(binding)
            }
        }
    }

    class ViewHolderImgUser private constructor(val binding: ImageItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserMessages) {
            binding.chat = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolderImgUser {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageItemUserBinding.inflate(layoutInflater, parent, false)
                return ViewHolderImgUser(binding)
            }
        }
    }
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
    abstract val message: String

    data class UserMessage(val userMessage: UserMessages) : DataItem() {
        override val id = userMessage.id
        override val uid = userMessage.uid
        override val message = userMessage.message
    }

    data class ContactMessage(val userMessage: UserMessages) : DataItem() {
        override val id = userMessage.id
        override val uid = userMessage.uid
        override val message = userMessage.message
    }
}


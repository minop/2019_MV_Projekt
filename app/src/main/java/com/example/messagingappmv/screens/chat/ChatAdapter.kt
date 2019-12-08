package com.example.messagingappmv.screens.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.UserMessages
import com.example.messagingappmv.databinding.*

import kotlinx.android.synthetic.main.fragment_chat.*
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
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
    private val ITEM_VIEW_TYPE_IMAGE_CONTACT = 2
    private val ITEM_VIEW_TYPE_IMAGE_USER = 3



    private var _notification = MutableLiveData<Boolean?>()

    val notification: LiveData<Boolean?>
        get() = _notification

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
            is ViewHolder3 -> {
                val nightItem = getItem(position) as DataItem.UserMessage
                holder.bind(nightItem.userMessage)
            }
            is ViewHolder4 -> {
                val nightItem = getItem(position) as DataItem.UserMessage
                holder.bind(nightItem.userMessage)
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
            ITEM_VIEW_TYPE_IMAGE_CONTACT -> ViewHolder4.from(parent)
            ITEM_VIEW_TYPE_IMAGE_USER -> ViewHolder3.from(parent)
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

//                _notification.value = true


            }
        }
    }


    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is DataItem.ContactMessage -> ITEM_VIEW_TYPE_HEADER
//            is DataItem.UserMessage -> ITEM_VIEW_TYPE_ITEM
//        }
        val message = getItem(position).message
        if (message != "") {
            if (message.length >= 5) {
                if (message.substring(0, 4) == "gif:") {
                    if (getItem(position).uid == uid) {
                        return ITEM_VIEW_TYPE_IMAGE_USER
                    } else {
                        return ITEM_VIEW_TYPE_IMAGE_CONTACT
                    }
                }
            }
            if (getItem(position).uid == uid) {
                return ITEM_VIEW_TYPE_ITEM
            } else {
                return ITEM_VIEW_TYPE_HEADER
            }

        } else return -1

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

    class ViewHolder3 private constructor(val binding: ImageItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserMessages) {
            binding.chat = item
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder3 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageItemContactBinding.inflate(layoutInflater, parent, false)
                return ViewHolder3(binding)
            }
        }
    }

    class ViewHolder4 private constructor(val binding: ImageItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserMessages) {
            binding.chat = item
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder4 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageItemUserBinding.inflate(layoutInflater, parent, false)
                return ViewHolder4(binding)
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


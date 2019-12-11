package com.example.messagingappmv.screens.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.database.AppRoomPosts
import com.example.messagingappmv.databinding.ListItemRoomOtherPostBinding
import com.example.messagingappmv.databinding.ListItemRoomPostBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppRoomAdapter(val clickListener: RoomListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(RoomDiffCallback()) {
    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1
    private val adapterScope = CoroutineScope(Dispatchers.Default)


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.RoomPost
                holder.bind(nightItem.appRoomPost, clickListener)
            }
            is ViewHolder2 -> {
                val nightItem = getItem(position) as DataItem.RoomPost
                holder.bind(nightItem.appRoomPost, clickListener)
            }
        }
//        holder.bind(getItem(position)!!, clickListener)
//        val current_post = getItem(position!!)
//        if(current_post.uid == 1L){
//            holder.itemView.room_name_string.setBackgroundColor(Color.parseColor("#000000"))
//        }
//        holder.itemView.room_name_string.setBackgroundColor(Color.parseColor("#000000"))
//        holder.itemView.room_name_string.setBackgroundColor(R.drawable.oval_for_one_post)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> ViewHolder2.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    fun addHeaderAndSubmitList(list: List<AppRoomPosts>?) {
        adapterScope.launch {
            //            val items = when (list) {
//                null -> listOf(DataItem.ContactPost)
//                else -> listOf(DataItem.ContactPost) + list.map { DataItem.RoomPost(it) }
//            }
            val items = list?.map { DataItem.RoomPost(it) }


            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is DataItem.ContactPost -> ITEM_VIEW_TYPE_HEADER
//            is DataItem.RoomPost -> ITEM_VIEW_TYPE_ITEM
//        }
        if(getItem(position).uid == 8L){
            return ITEM_VIEW_TYPE_ITEM
        } else {
            return ITEM_VIEW_TYPE_HEADER
        }
    }

    class ViewHolder private constructor(val binding: ListItemRoomPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppRoomPosts, clickListener: RoomListener) {
            binding.appRoom = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomPostBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class ViewHolder2 private constructor(val binding: ListItemRoomOtherPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppRoomPosts, clickListener: RoomListener) {
            binding.appRoom = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder2 {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRoomOtherPostBinding.inflate(layoutInflater, parent, false)
                return ViewHolder2(binding)
            }
        }
    }

//    class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        companion object {
//            fun from(parent: ViewGroup): TextViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val view = layoutInflater.inflate(R.layout.list_item_room_list, parent, false)
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


class RoomListener(val clickListener: (roomId: Long) -> Unit) {
    fun onClick(appRoomPosts: AppRoomPosts) = clickListener(appRoomPosts.id)
}

sealed class DataItem {
    abstract val id: Long
    abstract val uid: Long

    data class RoomPost(val appRoomPost: AppRoomPosts) : DataItem() {
        override val id = appRoomPost.id
        override val uid = appRoomPost.uid
    }

    data class ContactPost(val appRoomPost: AppRoomPosts): DataItem() {
        override val id = appRoomPost.id
        override val uid = appRoomPost.uid
    }
}


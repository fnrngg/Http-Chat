package com.example.httpchat.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.httpchat.databinding.ItemMessageFromBinding

class MessagesRecyclerAdapter : RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder>() {
    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    private var conversations: MutableList<String> =
        mutableListOf("isao", "esao", "da", "arao", "isao", "esao", "da", "arao")

    var itemClickedListener: ((user: String) -> Unit)? = null

    var itemDeleteListener: ((userId: String) -> Unit)? = null

    var lastItemLoadedListener: (() -> Unit)? = null

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMessageFromBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return conversations.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewBinderHelper.bind(
            holder.binding.swipeLayout,
            conversations[position] + position.toString()
        )
        holder.bindData(position)
    }

    fun setData(conversations: List<String>) {
        this.conversations = conversations.toMutableList()
        notifyItemRangeInserted(
            this.conversations.count() - conversations.count(),
            conversations.count()
        )
    }

    inner class ViewHolder(val binding: ItemMessageFromBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.conversation.setOnClickListener {
                itemClickedListener?.invoke(conversations[adapterPosition])
            }

            binding.deleteImage.setOnClickListener {
                itemDeleteListener?.invoke(conversations[adapterPosition])
            }
        }

        fun bindData(position: Int) {
            binding.deleteImage.setOnClickListener {
                conversations.removeAt(position)
                notifyDataSetChanged()
            }
            if (position == conversations.count() - 1) {
                lastItemLoadedListener?.invoke()
            }
        }
    }
}
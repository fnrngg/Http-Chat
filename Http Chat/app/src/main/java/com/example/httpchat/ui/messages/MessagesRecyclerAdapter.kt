package com.example.httpchat.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.httpchat.databinding.ItemMessageFromBinding

class MessagesRecyclerAdapter: RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder>() {
    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    private var conversations: MutableList<String> = mutableListOf("isao", "esao", "da", "arao", "isao", "esao", "da", "arao")

    var itemClickedListener: ((User: String) -> Unit)? = null

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageFromBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return conversations.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewBinderHelper.bind(holder.binding.swipeLayout, conversations[position] + position.toString())
        holder.bindData(position)
    }

    fun setData(conversations: MutableList<String>) {
        this.conversations = conversations
    }

    inner class ViewHolder(val binding: ItemMessageFromBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.conversation.setOnClickListener {
                itemClickedListener?.invoke(conversations[adapterPosition])
            }
        }

        fun bindData(position: Int) {
            binding.deleteImage.setOnClickListener {
                conversations.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }
}
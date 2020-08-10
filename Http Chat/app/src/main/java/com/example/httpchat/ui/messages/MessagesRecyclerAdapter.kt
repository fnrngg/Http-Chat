package com.example.httpchat.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.httpchat.databinding.ItemMessageFromBinding

class MessagesRecyclerAdapter: RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder>() {
    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()
    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageFromBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 9
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        holder.bindData(position)
    }

    inner class ViewHolder(val binding: ItemMessageFromBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {

        }
    }
}
package com.example.httpchat.ui.conversation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.httpchat.databinding.ItemSenderMessageBinding

class ConversationRecyclerAdapter: RecyclerView.Adapter<ConversationRecyclerAdapter.ViewHolder>() {

    private var conversation: List<String> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemSenderMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 14
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }

    fun setData(conversation: List<String>?) {
        conversation?.let {
            this.conversation = it
        }
    }

    inner class ViewHolder(binding: ItemSenderMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int) {

        }
    }
}
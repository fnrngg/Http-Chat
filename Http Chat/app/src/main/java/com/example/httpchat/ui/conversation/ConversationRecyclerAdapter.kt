package com.example.httpchat.ui.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.httpchat.R
import com.example.httpchat.databinding.ItemMyMessageBinding
import com.example.httpchat.databinding.ItemSenderMessageBinding
import com.example.httpchat.models.responses.Message

class ConversationRecyclerAdapter: RecyclerView.Adapter<ConversationRecyclerAdapter.ViewHolder>() {

    private var conversation: List<Message> = listOf()
    private lateinit var myId: String

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return if (viewType == 0) {
            val binding = ItemMyMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding.root)
        } else {
            val binding = ItemSenderMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding.root)
        }
    }

    override fun getItemCount(): Int {
        return conversation.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (conversation[position].from.toString() == myId) {
            0
        } else {
            1
        }
    }

    fun setData(conversation: List<Message>, myId: String) {
        this.myId = myId
        conversation.let {
            this.conversation = it
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val messageText = itemView.findViewById<TextView>(R.id.message_text)
        private val sentTimeText = itemView.findViewById<TextView>(R.id.sent_time_text)

        fun bindData(position: Int) {
            messageText.text = conversation[position].text
            sentTimeText.text = conversation[position].dateMillis.toString()
        }
    }
}
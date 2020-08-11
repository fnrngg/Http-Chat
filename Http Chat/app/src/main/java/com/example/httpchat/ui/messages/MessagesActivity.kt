package com.example.httpchat.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httpchat.databinding.ActivityMessagesBinding
import com.example.httpchat.ui.conversation.ConversationActivity

class MessagesActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MessagesActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityMessagesBinding
    private lateinit var adapter: MessagesRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.messagesRecycler.layoutManager = LinearLayoutManager(this)
        adapter = MessagesRecyclerAdapter()
        binding.messagesRecycler.adapter = adapter
        adapter.itemClickedListener = {user ->
            ConversationActivity.start(this, user)
        }

    }
}
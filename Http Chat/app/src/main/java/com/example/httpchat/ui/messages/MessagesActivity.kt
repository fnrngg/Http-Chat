package com.example.httpchat.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httpchat.databinding.ActivityMessagesBinding

class MessagesActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MessagesActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.messagesRecycler.layoutManager = LinearLayoutManager(this)
        binding.messagesRecycler.adapter =
            MessagesRecyclerAdapter()
    }
}
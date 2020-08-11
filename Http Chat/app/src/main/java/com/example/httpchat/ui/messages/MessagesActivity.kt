package com.example.httpchat.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httpchat.databinding.ActivityMessagesBinding
import com.example.httpchat.models.responses.UserAndMessageThumbnail
import com.example.httpchat.ui.conversation.ConversationActivity

class MessagesActivity : AppCompatActivity(), MessagesContract.View {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MessagesActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityMessagesBinding
    private lateinit var adapter: MessagesRecyclerAdapter

    private lateinit var presenter: MessagesContract.Presenter

    private val conversations = mutableListOf<UserAndMessageThumbnail>()

    private var loadedNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = MessagesPresenterImpl(this)
        setupRecycler()
        presenter.getConversations("", loadedNum)

        setupSearch()
    }

    private fun setupSearch() {
        binding.messagesSearch.doAfterTextChanged {
            presenter.getConversations(binding.messagesSearch.text.toString(), loadedNum)
        }
    }

    override fun onStart() {
        super.onStart()
        loadedNum = 0
    }

    private fun setupRecycler() {
        binding.messagesRecycler.layoutManager = LinearLayoutManager(this)
        adapter = MessagesRecyclerAdapter()
        binding.messagesRecycler.adapter = adapter
        adapter.itemClickedListener = { user ->
            ConversationActivity.start(this, user)
        }

        adapter.itemDeleteListener = {userId ->
            presenter.deleteConversation(userId)
        }

        adapter.lastItemLoadedListener = {
            presenter.getConversations(loadedNum = loadedNum)
        }
    }

    override fun setConversations(conversations: List<UserAndMessageThumbnail>) {
        this.conversations += conversations
        if (conversations.isNotEmpty()) {
            loadedNum++
        }
        adapter.setData(this.conversations)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

}
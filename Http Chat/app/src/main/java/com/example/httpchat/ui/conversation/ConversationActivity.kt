package com.example.httpchat.ui.conversation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httpchat.R
import com.example.httpchat.databinding.ActivityConversationBinding
import com.example.httpchat.models.responses.Message


class ConversationActivity : AppCompatActivity(), ConversationContract.View {

    companion object {
        private const val MESSAGE_FROM_USER ="MESSAGE_FROM_USER"

        fun start(context: Context, user: String) {
            val  intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra(MESSAGE_FROM_USER, user)
            context.startActivity(intent)
        }
    }
    
    private lateinit var binding: ActivityConversationBinding

    private lateinit var adapter: ConversationRecyclerAdapter

    private lateinit var presenter: ConversationPresenterImpl
    private lateinit var userId: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra(MESSAGE_FROM_USER)!!
        binding.sendMessageTextField.onRightDrawableClicked {
            Toast.makeText(this, "isao esao da arao", Toast.LENGTH_SHORT).show()
        }

        presenter = ConversationPresenterImpl(this)
        setupViews()
        presenter.getConversation(userId)

    }

    private fun setupViews() {
        setupRecycler()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)

        binding.sendMessageTextField.onRightDrawableClicked {
            presenter.sendMessage(it.text.toString())
        }
    }

    private fun setupRecycler() {
        adapter = ConversationRecyclerAdapter()
        binding.messagesRecycler.adapter = adapter
        binding.messagesRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }

    override fun setConversation(conversation: List<Message>, myId: String) {
        adapter.setData(conversation.asReversed(), myId)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }

}
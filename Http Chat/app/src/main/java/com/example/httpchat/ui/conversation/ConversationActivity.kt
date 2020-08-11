package com.example.httpchat.ui.conversation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.httpchat.R
import com.example.httpchat.databinding.ActivityConversationBinding
import com.example.httpchat.models.responses.Message
import com.example.httpchat.models.responses.User


class ConversationActivity : AppCompatActivity(), ConversationContract.View {

    companion object {
        private const val MESSAGE_FROM_USER ="MESSAGE_FROM_USER"

        fun start(context: Context, user: User) {
            val  intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra(MESSAGE_FROM_USER, user)
            context.startActivity(intent)
        }
    }
    
    private lateinit var binding: ActivityConversationBinding

    private lateinit var adapter: ConversationRecyclerAdapter

    private lateinit var presenter: ConversationPresenterImpl
    private lateinit var user: User
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra(MESSAGE_FROM_USER)!!
        binding.sendMessageTextField.onRightDrawableClicked {
            Toast.makeText(this, "isao esao da arao", Toast.LENGTH_SHORT).show()
        }

        presenter = ConversationPresenterImpl(this)
        setupViews()
        presenter.getConversation(user.id.toString())

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

        if (!user.picture.isNullOrEmpty()) {
            val imageAsBytes: ByteArray = Base64.decode(user.picture, Base64.DEFAULT)
            val image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.count())
            Glide.with(binding.root.context)
                .load(image)
                .into(binding.userImage)
        }
        binding.collapsingToolbar.apply {
            title = user.name
            subtitle = user.profession
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
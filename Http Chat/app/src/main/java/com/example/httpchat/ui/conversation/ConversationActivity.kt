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
import com.bumptech.glide.Glide
import com.example.httpchat.R
import com.example.httpchat.databinding.ActivityConversationBinding


class ConversationActivity : AppCompatActivity() {

    companion object {
        private const val MESSAGE_FROM_USER ="MESSAGE_FROM_USER"

        fun start(context: Context, user: String) {
            val  intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra(MESSAGE_FROM_USER, user)
            context.startActivity(intent)
        }
    }
    
    private lateinit var binding: ActivityConversationBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.sendMessageTextField.onRightDrawableClicked {
            Toast.makeText(this, "isao esao da arao", Toast.LENGTH_SHORT).show()
        }

        binding.messagesRecycler.adapter =
            ConversationRecyclerAdapter()
        binding.messagesRecycler.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_left)

        val url: String? = ""
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.icons_user_male)
            .circleCrop()
            .into(binding.userImage)

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
}
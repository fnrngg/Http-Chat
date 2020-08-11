package com.example.httpchat.ui.checkuser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.httpchat.R
import com.example.httpchat.preferences.PreferencesManager
import com.example.httpchat.ui.messages.MessagesActivity
import com.example.httpchat.ui.register.RegisterActivity

class CheckUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_user)
        val user = PreferencesManager.getUser()
        if (user.isEmpty()) {
            RegisterActivity.start(this)
        } else {
            MessagesActivity.start(this)
        }
    }
}
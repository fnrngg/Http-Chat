package com.example.httpchat.ui.checkuser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.httpchat.R
import com.example.httpchat.ui.messages.MessagesActivity
import com.example.httpchat.ui.register.RegisterActivity

class CheckUserActivity : AppCompatActivity() {
    companion object {
        private const val USER_ALREADY_REGISTERED = "USER_ALREADY_REGISTERED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_user)
        val user = PreferenceManager.getDefaultSharedPreferences(this).getString(
            USER_ALREADY_REGISTERED, null)
        if (user.isNullOrEmpty()) {
            RegisterActivity.start(this)
        } else {
            MessagesActivity.start(this)
        }
    }
}
package com.example.httpchat.ui.register

import com.example.httpchat.models.User

interface RegisterContract {
    interface View {
        fun startActivity()
    }

    interface Presenter {
        fun saveUser(user: User)
        fun dispose()
    }
}
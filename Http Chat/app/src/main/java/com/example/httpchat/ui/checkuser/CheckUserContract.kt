package com.example.httpchat.ui.checkuser

interface CheckUserContract {
    interface View {
        fun giveUserId(userId: String)
    }

    interface Presenter {
        fun getUserId()
    }
}
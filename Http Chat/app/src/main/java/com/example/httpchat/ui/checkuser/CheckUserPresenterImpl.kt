package com.example.httpchat.ui.checkuser

import com.example.httpchat.preferences.PreferencesManager

class CheckUserPresenterImpl(private val view: CheckUserContract.View): CheckUserContract.Presenter {
    override fun getUserId() {
        view.giveUserId(PreferencesManager.getUser())
    }

}
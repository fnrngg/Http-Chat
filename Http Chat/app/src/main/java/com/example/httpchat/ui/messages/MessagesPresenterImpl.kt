package com.example.httpchat.ui.messages

import com.example.httpchat.preferences.PreferencesManager
import com.example.httpchat.retrofit.Api
import com.example.httpchat.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MessagesPresenterImpl(private val view: MessagesContract.View) :
    MessagesContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    private val myId = PreferencesManager.getUser()

    private val service = RetrofitClient.retrofit.create(Api::class.java)

    override fun getConversations(nickname: String, loadedNum: Int) {
        compositeDisposable.add(
            service.getAllConversations(myId, nickname, loadedNum).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view.setConversations(it)
                    },
                    {
                    })
        )
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }

    override fun deleteConversation(userId: String) {
        compositeDisposable.add(
            service.deleteConversation(myId, userId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {})
        )
    }
}
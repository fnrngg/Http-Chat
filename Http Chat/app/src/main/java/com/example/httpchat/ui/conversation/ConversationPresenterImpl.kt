package com.example.httpchat.ui.conversation

import com.example.httpchat.models.parameterclasses.sendMessageParams
import com.example.httpchat.models.requests.LoadConversationRequest
import com.example.httpchat.preferences.PreferencesManager
import com.example.httpchat.retrofit.Api
import com.example.httpchat.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ConversationPresenterImpl(private val view: ConversationContract.View) :
    ConversationContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    private val myId = PreferencesManager.getUser()

    private lateinit var userId: String

    private val service = RetrofitClient.retrofit.create(Api::class.java)


    override fun getConversation(userId: String) {
        this.userId = userId
        compositeDisposable.add(
            service.loadConversation(LoadConversationRequest(myId.toLong(), userId.toLong()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view.setConversation(it, myId)
                    },
                    {
                    })
        )
    }


    override fun dispose() {
        compositeDisposable.dispose()
    }

    override fun sendMessage(message: String) {
        compositeDisposable.add(
            service.sendMessage(sendMessageParams(myId.toLong(), userId.toLong(), message))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getConversation(userId)
                }, {})
        )
    }


}
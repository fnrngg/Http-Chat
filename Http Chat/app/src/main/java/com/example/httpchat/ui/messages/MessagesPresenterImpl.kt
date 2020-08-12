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

    override fun getConversations(loadedNum: Int) {
        compositeDisposable.add(
            service.loadConversationHistory(myId.toLong(), loadedNum.toLong())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view.setConversations(it)
                    },
                    {
                    })
        )
    }


    override fun searchConversations(nickname: String) {
        compositeDisposable.add(
            service.searchConversation(myId.toLong(), nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view.setSearchResults(it)
                    },
                    {
                    })
        )
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }

    override fun deleteConversation(userMappingId: Long) {
        compositeDisposable.add(
            service.deleteConversation(myId.toLong(), userMappingId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {})
        )
    }
}
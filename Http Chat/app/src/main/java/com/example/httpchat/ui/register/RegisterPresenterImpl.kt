package com.example.httpchat.ui.register

import com.example.httpchat.models.User
import com.example.httpchat.preferences.PreferencesManager
import com.example.httpchat.retrofit.Api
import com.example.httpchat.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RegisterPresenterImpl(private val view: RegisterContract.View) : RegisterContract.Presenter {

    private val compositeDisposable = CompositeDisposable()
    override fun saveUser(user: User) {
        val service = RetrofitClient.retrofit.create(Api::class.java)
        compositeDisposable.add(
            service.saveUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        PreferencesManager.saveUser(it)
                        view.startActivity()
                    },
                    {
                    })
        )
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }


}

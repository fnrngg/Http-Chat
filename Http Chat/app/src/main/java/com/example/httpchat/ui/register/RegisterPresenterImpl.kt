package com.example.httpchat.ui.register

import android.util.Log
import com.example.httpchat.models.User
import com.example.httpchat.models.requests.UserLoginRequest
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
            service.userLogin(UserLoginRequest(user.nickname,user.profession,user.image))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        PreferencesManager.saveUser(it)
                        view.startActivity()
                    },
                    {
                        Log.d("ragaca","ragaca")
                    })
        )
    }

    override fun dispose() {
        compositeDisposable.dispose()
    }


}

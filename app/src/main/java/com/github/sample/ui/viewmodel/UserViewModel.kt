package com.github.sample.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.github.sample.data.api.RetrofitService
import com.github.sample.data.api.UserApi
import com.github.sample.data.models.User
import com.github.sample.data.models.UserAlbum
import io.reactivex.Observable

class UserViewModel : ViewModel() {

    private var userApi: UserApi? = null

    var albumId = ObservableField<Int>()
    var photoId = ObservableField<Int>()
    var userAlbumTitle = ObservableField<String>()
    var userAlbumUrl = ObservableField<String>()
    var isOnline = ObservableField<Boolean>()

    fun init() {
        userApi = RetrofitService.createService(UserApi::class.java)
    }

    fun getUserList(): Observable<List<User>> {
        return userApi?.getUsersList()!!
    }

    fun getUserAlbumListByUserId(userId: Int): Observable<List<UserAlbum>> {
        albumId.set(userId)
        return userApi?.getUserAlbumListByAlbumId(userId)!!
    }

    fun getUserAlbumDetail(albumId: Int, photoId: Int): Observable<List<UserAlbum>> {
        return userApi?.getUserAlbumListByIds(albumId, photoId)!!
    }
}
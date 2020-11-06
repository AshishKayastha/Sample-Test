package com.github.sample.data.api

import com.github.sample.data.models.User
import com.github.sample.data.models.UserAlbum
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {

    @GET("users")
    fun getUsersList(): Observable<List<User>>

    @GET("photos")
    fun getUserAlbumListByAlbumId(@Query("albumId") albumId: Int?): Observable<List<UserAlbum>>

    @GET("photos")
    fun getUserAlbumListByIds(
        @Query("albumId") albumId: Int?,
        @Query("id") photoId: Int?
    ): Observable<List<UserAlbum>>
}
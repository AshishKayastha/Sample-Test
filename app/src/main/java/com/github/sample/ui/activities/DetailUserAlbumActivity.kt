package com.github.sample.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.sample.R
import com.github.sample.data.models.UserAlbum
import com.github.sample.databinding.ActivityDetailUserAlbumBinding
import com.github.sample.ui.viewmodel.UserViewModel
import com.github.sample.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail_user_album.*

class DetailUserAlbumActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityDetailUserAlbumBinding
    private var compositeDisposable: CompositeDisposable? = null

    private var albumId: Int = 0
    private var photoId: Int = 0

    companion object {
        private const val EXTRA_ALBUM_ID = "album_id"
        private const val EXTRA_PHOTO_ID = "photo_id"

        fun createIntent(context: Context, albumId: Int?, photoId: Int?): Intent {
            return Intent(context, DetailUserAlbumActivity::class.java)
                .putExtra(EXTRA_ALBUM_ID, albumId)
                .putExtra(EXTRA_PHOTO_ID, photoId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail_user_album)
        viewBinding.viewModel =
            ViewModelProvider(this).get(UserViewModel::class.java).apply { init() }

        albumId = if (intent?.extras != null && intent?.extras!!.containsKey(EXTRA_ALBUM_ID)) {
            intent.extras!!.getInt(EXTRA_ALBUM_ID)
        } else 0

        photoId = if (intent?.extras != null && intent?.extras!!.containsKey(EXTRA_PHOTO_ID)) {
            intent.extras!!.getInt(EXTRA_PHOTO_ID)
        } else 0

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        compositeDisposable = CompositeDisposable()
        loadUserAlbum()
    }

    private fun loadUserAlbum() {
        if (Utils.isOnline(this)) {
            viewBinding.viewModel?.isOnline?.set(true)

            viewBinding.viewModel?.albumId?.set(albumId)
            viewBinding.viewModel?.photoId?.set(photoId)

            compositeDisposable?.add(
                viewBinding.viewModel?.getUserAlbumDetail(albumId!!, photoId!!)!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showUserAlbumList)
            )
        } else {
            viewBinding.viewModel?.isOnline?.set(false)
        }
    }

    private fun showUserAlbumList(userAlbumList: List<UserAlbum>) {
        if (userAlbumList.isNotEmpty()) {
            viewBinding.viewModel?.apply {
                userAlbumTitle.set(userAlbumList.first().title)
                userAlbumUrl.set(userAlbumList.first().url)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EXTRA_ALBUM_ID, albumId)
        outState.putInt(EXTRA_PHOTO_ID, photoId)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finishAfterTransition()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }
}
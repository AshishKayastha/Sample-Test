package com.github.sample.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sample.R
import com.github.sample.data.models.UserAlbum
import com.github.sample.databinding.ActivityUserAlbumListBinding
import com.github.sample.databinding.ListItemUserAlbumBinding
import com.github.sample.ui.adapters.UserModelAdapter
import com.github.sample.ui.listener.OnClickListener
import com.github.sample.ui.viewmodel.UserViewModel
import com.github.sample.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_album_list.*

class UserAlbumListActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityUserAlbumListBinding
    private lateinit var adapter: UserModelAdapter<UserAlbum, ListItemUserAlbumBinding>

    private var compositeDisposable: CompositeDisposable? = null

    private var userId: Int = 0

    companion object {
        private const val EXTRA_USER_ID = "user_id"

        fun createIntent(context: Context, userId: Int?): Intent {
            return Intent(context, UserAlbumListActivity::class.java)
                .putExtra(EXTRA_USER_ID, userId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_album_list)
        viewBinding.viewModel =
            (ViewModelProvider(this).get(UserViewModel::class.java)).apply { init() }

        userId = if (intent?.extras != null && intent?.extras!!.containsKey(EXTRA_USER_ID)) {
            intent.extras!!.getInt(EXTRA_USER_ID)
        } else 0

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Album ID: $userId"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        compositeDisposable = CompositeDisposable()
        loadUserAlbumList()
    }

    private fun loadUserAlbumList() {
        if (Utils.isOnline(this)) {
            viewBinding.viewModel?.isOnline?.set(true)

            val userAlbumList = userId.let { viewBinding.viewModel?.getUserAlbumListByUserId(it) }

            compositeDisposable?.add(
                userAlbumList!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { viewBinding.progressBar.visibility = View.VISIBLE }
                    .doFinally { viewBinding.progressBar.visibility = View.GONE }
                    .subscribe(this::showUserAlbumList)
            )
        } else {
            viewBinding.viewModel?.isOnline?.set(false)
        }
    }

    private fun showUserAlbumList(userAlbumList: List<UserAlbum>) {
        adapter = UserModelAdapter(
            userAlbumList.toMutableList(),
            R.layout.list_item_user_album
        ) { binding, model ->
            binding.userAlbum = model
            binding.listener = object : OnClickListener<UserAlbum> {
                override fun onClick(userAlbum: UserAlbum) {
                    startActivity(
                        DetailUserAlbumActivity.createIntent(
                            this@UserAlbumListActivity,
                            userAlbum.albumId,
                            userAlbum.id
                        )
                    )
                }
            }
        }

        viewBinding.userAlbumList.adapter = adapter
        viewBinding.userAlbumList.layoutManager = LinearLayoutManager(this)
        viewBinding.userAlbumList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EXTRA_USER_ID, userId)
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
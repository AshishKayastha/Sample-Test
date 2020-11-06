package com.github.sample.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sample.R
import com.github.sample.data.models.User
import com.github.sample.databinding.ActivityUserInfoListBinding
import com.github.sample.databinding.ListItemUserInfoBinding
import com.github.sample.ui.adapters.UserModelAdapter
import com.github.sample.ui.listener.OnClickListener
import com.github.sample.ui.viewmodel.UserViewModel
import com.github.sample.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_info_list.*
import androidx.recyclerview.widget.DividerItemDecoration

class UserInfoListActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityUserInfoListBinding
    private lateinit var adapter: UserModelAdapter<User, ListItemUserInfoBinding>

    private var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_info_list)
        viewBinding.viewModel =
            (ViewModelProvider(this).get(UserViewModel::class.java)).apply { init() }

        setSupportActionBar(toolbar)

        compositeDisposable = CompositeDisposable()
        loadUserList()
    }

    private fun loadUserList() {
        if (Utils.isOnline(this)) {
            viewBinding.viewModel?.isOnline?.set(true)

            compositeDisposable?.add(
                viewBinding.viewModel?.getUserList()!!
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { viewBinding.progressBar.visibility = View.VISIBLE }
                    .doFinally { viewBinding.progressBar.visibility = View.GONE }
                    .subscribe(this::showUserInfoList)
            )
        } else {
            viewBinding.viewModel?.isOnline?.set(false)
        }
    }

    private fun showUserInfoList(userInfoList: List<User>) {
        adapter = UserModelAdapter(
            userInfoList.toMutableList(),
            R.layout.list_item_user_info
        ) { binding, model ->
            binding.user = model
            binding.listener = object : OnClickListener<User> {
                override fun onClick(user: User) {
                    startActivity(
                        UserAlbumListActivity.createIntent(
                            this@UserInfoListActivity,
                            user.id
                        )
                    )
                }
            }
        }

        viewBinding.userList.adapter = adapter
        viewBinding.userList.layoutManager = LinearLayoutManager(this)
        viewBinding.userList.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }
}
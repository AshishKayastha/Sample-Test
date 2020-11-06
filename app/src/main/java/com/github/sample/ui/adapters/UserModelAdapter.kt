package com.github.sample.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

class UserModelAdapter<T : Any, V : ViewDataBinding>(
    private var items: MutableList<T>,
    private val layoutId: Int,
    private val bindData: (V, T) -> Unit
) : RecyclerView.Adapter<UserModelAdapter.UserModelHolder<T, V>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelHolder<T, V> {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return UserModelHolder(view, bindData)
    }

    override fun onBindViewHolder(holder: UserModelHolder<T, V>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class UserModelHolder<in T : Any, in V : ViewDataBinding>(
        view: View,
        private val bindData: (V, T) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val dataBinding: V? = DataBindingUtil.bind(view)

        fun bind(item: T) {
            dataBinding?.let {
                bindData(it, item)
                it.executePendingBindings()
            }
        }
    }
}
package com.github.sample.ui.listener

interface OnClickListener<in T : Any> {

    fun onClick(data: T)
}
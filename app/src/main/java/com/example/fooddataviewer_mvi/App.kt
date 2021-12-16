package com.example.fooddataviewer_mvi

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.fooddataviewer_mvi.di.ApplicationComponent
import com.example.fooddataviewer_mvi.di.DaggerApplicationComponent
import com.singhajit.sherlock.core.Sherlock
import kotlin.reflect.KClass

class App: Application(){

    val component by lazy {
        DaggerApplicationComponent.builder().context(this).build()
    }

    override fun onCreate() {
        super.onCreate()
        Sherlock.init(this);
    }
}

val Context.component: ApplicationComponent
    get() = (this.applicationContext as App).component

fun <T, M ,E> Fragment.getViewModel(type:KClass<T>): ViewModelInt<M,E> where T: ViewModel, T: ViewModelInt<M,E>{
    val factory = this.context?.component?.viewModelFactory()
    return ViewModelProviders.of(this, factory)[type.java]
}

fun <T, M ,E> FragmentActivity.getViewModel(type:KClass<T>): ViewModelInt<M,E> where T: ViewModel, T: ViewModelInt<M,E>{
    val factory = this.applicationContext?.component?.viewModelFactory()
    return ViewModelProviders.of(this, factory)[type.java]
}


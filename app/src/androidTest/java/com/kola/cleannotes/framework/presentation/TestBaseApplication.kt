package com.kola.cleannotes.framework.presentation

import com.kola.cleannotes.di.DaggerAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TestBaseApplication : BaseApplication() {
    override fun initAppComponent() {
        appComponent = DaggerAppComponent.factory().create(this)
    }
}
package com.kola.cleannotes.di

import com.kola.cleannotes.business.TempTest
import com.kola.cleannotes.framework.datasource.cache.NoteDaoServiceTest
import com.kola.cleannotes.framework.datasource.network.NoteFirestoreServiceTests
import com.kola.cleannotes.framework.presentation.TestBaseApplication
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@Singleton
@FlowPreview
@ExperimentalCoroutinesApi
@Component(
    modules = [
    AppModule::class,TestModule::class]
)

interface TestAppComponent: AppComponent{
    @Component.Factory
    interface  Factory{
        fun create(@BindsInstance app: TestBaseApplication): TestAppComponent
    }

    fun inject(tempTest: NoteFirestoreServiceTests)
    fun inject(tempTest: NoteDaoServiceTest)
}
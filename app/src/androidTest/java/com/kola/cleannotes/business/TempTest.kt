package com.kola.cleannotes.business

import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.firebase.firestore.FirebaseFirestore
import com.kola.cleannotes.di.TestAppComponent
import com.kola.cleannotes.framework.presentation.TestBaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4ClassRunner::class)
class TempTest {
    val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext() as TestBaseApplication

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    init {
        (application.appComponent as TestAppComponent).inject(this)
    }

    @Test
    fun someRandomTest(){
        assert(::firebaseFirestore.isInitialized)
    }



}
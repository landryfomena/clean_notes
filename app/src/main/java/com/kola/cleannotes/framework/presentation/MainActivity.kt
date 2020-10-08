package com.kola.cleannotes.framework.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kola.cleannotes.R
import com.kola.cleannotes.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity()
{

    private val TAG: String = "AppDebug"

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        printLogD("MainActivity", "FirebaseAuth: ${firebaseAuth}")
    }

}
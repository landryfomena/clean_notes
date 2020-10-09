package com.kola.cleannotes.di

import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.kola.cleannotes.business.domain.model.NoteFactory
import com.kola.cleannotes.framework.datasource.cache.database.NoteDatabase
import com.kola.cleannotes.framework.datasource.data.NoteDataFactory
import com.kola.cleannotes.framework.presentation.TestBaseApplication
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object TestModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDb(app: TestBaseApplication): NoteDatabase {
        return Room.inMemoryDatabaseBuilder(app, NoteDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDataFactory(
        application: TestBaseApplication,
        noteFactory: NoteFactory
    ):NoteDataFactory {
        return NoteDataFactory(application, noteFactory)
    }

}
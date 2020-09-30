package com.kola.cleannotes.business.interactions.notelist

import com.kola.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.kola.cleannotes.business.domain.model.NoteFactory
import com.kola.cleannotes.business.domain.state.DataState
import com.kola.cleannotes.di.DependencyContainer
import com.kola.cleannotes.framework.presentation.notelist.state.NoteListStateEvent
import com.kola.cleannotes.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/*
1 getNumNotes_success_confirmCorrect()
  a) get number of notes in cache
  b) listen for GET_NUM_NOTES_SUCCESS from flow emission
  c) compare with number of notes in fake data set

 */
@InternalCoroutinesApi
class GetNumNotesTest {
    // system iin test
    private val getNumNotes: GetNumNotes

    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteFactory = dependencyContainer.noteFactory
        getNumNotes = GetNumNotes(
            noteCacheDataSource = noteCacheDataSource
        )
    }


    @Test
    fun getNumNotes_success_confirmCorrect() = runBlocking {
        var numNotes = 0
        getNumNotes.getNumNotes(
            stateEvent = NoteListStateEvent.GetNumNotesInCacheEvent()
        ).collect(object : FlowCollector<DataState<NoteListViewState>?> {
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assertEquals(
                    value?.stateMessage?.response?.message, GetNumNotes.GET_NUM_NOTES_SUCCESS
                )
            }


        })
        val actualNumNotesInCache = noteCacheDataSource.getNumNotes()
        assertTrue(actualNumNotesInCache == numNotes)
    }
}
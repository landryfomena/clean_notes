package com.kola.cleannotes.business.interactions.notelist

import com.kola.cleannotes.business.data.cache.CacheResponseHandler
import com.kola.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.kola.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.kola.cleannotes.business.data.util.safeCacheCall
import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.business.domain.model.NoteFactory
import com.kola.cleannotes.business.domain.state.*
import com.kola.cleannotes.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class InsertNewNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
    private val noteFactory: NoteFactory

) {
    fun insertNote(
        id: String? = null,
        title: String,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {
        val newNote = noteFactory.createSingleNote(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            body = ""
        )

        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.insertNote(newNote)
        }

        val cacheResponse = object : CacheResponseHandler<NoteListViewState, Long>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Long): DataState<NoteListViewState>? {
                return if (resultObj > 0) {
                    val viewState = NoteListViewState(
                        newNote = newNote
                    )

                   DataState.data(
                        response = Response(
                            message = INSERT_NOTE_SUCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )


                } else {
                    DataState.data(
                        response = Response(
                            message = INSERT_NOTE_SUCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()




        emit(cacheResponse)
        updateNetwork(cacheResponse?.stateMessage?.response?.message ?: "", newNote)
    }

    private suspend fun updateNetwork(cacheResponse: String, newNote: Note) {
        if (cacheResponse.equals(INSERT_NOTE_SUCESS)) {
            noteNetworkDataSource.insertOrUpdateNote(newNote)
        }

    }

    companion object {
        const val INSERT_NOTE_SUCESS = " successfully inserted new note "
        const val INSERT_NEW_NOTE_FAILED = " failed to insert new note "
    }
}
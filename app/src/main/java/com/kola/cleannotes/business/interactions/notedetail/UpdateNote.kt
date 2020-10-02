package com.kola.cleannotes.business.interactions.notedetail

import com.kola.cleannotes.business.data.cache.CacheResponseHandler
import com.kola.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.kola.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.kola.cleannotes.business.data.util.safeApiCall
import com.kola.cleannotes.business.data.util.safeCacheCall
import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.business.domain.state.*
import com.kola.cleannotes.framework.presentation.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {
    fun updateNote(
        note: Note,
        stateEvent: StateEvent
    ): Flow<DataState<NoteDetailViewState>?> = flow {
        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.updateNote(
                primaryKey = note.id,
                newTitle = note.title,
                newBody = note.body
            )
        }
        val response = object : CacheResponseHandler<NoteDetailViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ) {
            override suspend fun handleSuccess(resultObj: Int): DataState<NoteDetailViewState>? {
                return if (resultObj > 0) {
                    DataState.data(
                        response = Response(
                            UPDATE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ), data = null,
                        stateEvent = stateEvent
                    )
                } else {
                    DataState.data(
                        response = Response(
                            UPDATE_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(response)
        updateNetwork(response?.stateMessage?.response?.message, note)
    }

    private suspend fun updateNetwork(response: String?, note: Note) {
        if (response.equals(UPDATE_NOTE_SUCCESS)) {
            safeApiCall(IO) {
                noteNetworkDataSource.insertOrUpdateNote(note)
            }
        }
    }

    companion object {
        const val UPDATE_NOTE_SUCCESS = "Successfully updated note"
        const val UPDATE_NOTE_FAILED = "Failed to update note."
        const val UPDATE_NOTE_FAILED_PK = "Update failed. Note is missing primary key"
    }

}
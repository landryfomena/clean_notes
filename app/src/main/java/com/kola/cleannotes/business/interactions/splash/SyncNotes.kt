package com.kola.cleannotes.business.interactions.splash

import com.kola.cleannotes.business.data.cache.CacheResponseHandler
import com.kola.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.kola.cleannotes.business.data.network.ApiResponseHandler
import com.kola.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.kola.cleannotes.business.data.util.safeApiCall
import com.kola.cleannotes.business.data.util.safeCacheCall
import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.business.domain.state.DataState
import com.kola.cleannotes.util.DateUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SyncNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {
    suspend fun syncNotes() {
        val cachedNotesList = getCachedNotes()
        syncNetorkNotesWithCachedNotes(ArrayList(cachedNotesList))
    }

    private suspend fun getCachedNotes(): List<Note> {
        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.getAllNotes()
        }

        val response = object : CacheResponseHandler<List<Note>, List<Note>>(
            response = cacheResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObj: List<Note>): DataState<List<Note>>? {
                return DataState.data(
                    response = null,
                    data = resultObj,
                    stateEvent = null
                )
            }

        }.getResult()
        return response?.data ?: ArrayList()
    }

    private suspend fun syncNetorkNotesWithCachedNotes(cachedNotes: ArrayList<Note>) =
        withContext(IO) {
            val networkResult = safeApiCall(IO) {
                noteNetworkDataSource.getAllNotes()
            }

            val response = object : ApiResponseHandler<List<Note>, List<Note>>(
                response = networkResult,
                stateEvent = null
            ) {
                override suspend fun handleSuccess(resultObj: List<Note>): DataState<List<Note>> {
                    return DataState.data(response = null, data = resultObj, stateEvent = null)
                }

            }.getResult()
            val noteList = response?.data ?: ArrayList()

            val job = launch {
                for (note in noteList) {
                    noteCacheDataSource.searchNoteById(note.id)?.let { cachedNote ->
                        cachedNotes.remove(cachedNote)
                        checkIfCachedNoteRequiresUpdate(cachedNote, note)
                    } ?: noteCacheDataSource.insertNote(note)
                }
            }
            job.join()
            for (cachedNote in cachedNotes) {
                safeApiCall(IO) {
                    noteNetworkDataSource.insertOrUpdateNote(cachedNote)
                }
            }

        }

    private suspend fun checkIfCachedNoteRequiresUpdate(cachedNote: Note, networkNote: Note) {
        val cacheUpdateAt = cachedNote.updated_at
        val networkUpdatedAt = networkNote.updated_at

        if (networkUpdatedAt > cacheUpdateAt) {
            safeCacheCall(IO) {
                noteCacheDataSource.updateNote(
                    primaryKey = networkNote.id,
                    newTitle = networkNote.title,
                    newBody = networkNote.body
                )
            }
        } else {
            safeApiCall(IO) {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }
        }
    }
}



package com.kola.cleannotes.business.interactions.notelist

import com.kola.cleannotes.business.data.cache.CacheResponseHandler
import com.kola.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.kola.cleannotes.business.data.util.safeCacheCall
import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.business.domain.state.*
import com.kola.cleannotes.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchNote(private val noteCacheDataSource: NoteCacheDataSource) {
    fun searchNotes(
        query: String,
        filterAndOrder: String,
        page: Int,
        stateEvent: StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow {
        var updatePage = page
        if (page <= 0) {
            updatePage = 1
        }
        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.searchNotes(
                query = query,
                filterAndOrder = filterAndOrder,
                page = updatePage
            )
        }
        val response = object: CacheResponseHandler<NoteListViewState,List<Note>>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override suspend fun handleSuccess(resultObj: List<Note>): DataState<NoteListViewState>? {
                var message:String ?= SEARCH_NOTES_SUCCESS
                var uiComponentType: UIComponentType = UIComponentType.None()
                if (resultObj.size == 0){
                    message = SEARCH_NOTES_NO_MATCHING_RESULTS
                    uiComponentType = UIComponentType.Toast()
                }

                return DataState.data(
                    response =  Response(
                        message= message,
                        uiComponentType =  uiComponentType,
                        messageType = MessageType.Success()
                    ),
                    data =  NoteListViewState(
                        noteList =  ArrayList(resultObj)
                    ),
                    stateEvent =  stateEvent
                )
            }

        }.getResult()
        emit(response)


    }
    companion object{
        val SEARCH_NOTES_SUCCESS = "Successfully retrieved list of notes."
        val SEARCH_NOTES_NO_MATCHING_RESULTS = "There are no notes that match that query."
        val SEARCH_NOTES_FAILED = "Failed to retrieve the list of notes."

    }
}
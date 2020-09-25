package com.kola.cleannotes.business.data.network.abstraction

import com.kola.cleannotes.business.domain.model.Note

interface NoteNetworkDataSource {
    suspend fun insertOrUpdateNote(note: Note)
    suspend fun deleteNote(primaryKey: String)
    suspend fun insertDeletedNote(note: Note)
    suspend fun insertDeletedNotes(notes: List<Note>)
    suspend fun deleteDeletedNotes(notes: List<Note>)
    suspend fun getDeletedNote()
    suspend fun getDeletedNotes(): List<Note>
    suspend fun searchNote(note: Note): Note?
    suspend fun getAllNotes(): List<Note>
    suspend fun insertOrUpdateNotes(notes: List<Note>)


}
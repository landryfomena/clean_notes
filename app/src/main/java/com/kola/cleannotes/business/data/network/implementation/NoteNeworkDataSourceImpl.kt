package com.kola.cleannotes.business.data.network.implementation

import com.kola.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.framework.datasource.network.abstraction.NoteFirestoreService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteNetworkDataSourceImpl @Inject constructor(
    private val firestoreService: NoteFirestoreService
) : NoteNetworkDataSource {
    override suspend fun insertOrUpdateNote(note: Note) = firestoreService.insertOrUpdateNote(note)
    override suspend fun deleteNote(primaryKey: String) = firestoreService.deleteNote(primaryKey)

    override suspend fun insertDeletedNote(note: Note) = firestoreService.insertDeletedNote(note)

    override suspend fun insertDeletedNotes(notes: List<Note>) =
        firestoreService.insertDeletedNotes(notes)

    override suspend fun deleteDeletedNotes(notes: List<Note>) =
        firestoreService.deleteDeletedNotes(notes)

    override suspend fun getDeletedNote() = firestoreService.getDeletedNote()

    override suspend fun getDeletedNotes() = firestoreService.getDeletedNotes()

    override suspend fun searchNote(note: Note) = firestoreService.searchNote(note)

    override suspend fun getAllNotes() = firestoreService.getAllNotes()

    override suspend fun insertOrUpdateNotes(notes: List<Note>) =
        firestoreService.insertOrUpdateNotes(notes)

    override suspend fun deleteDeletedNote(note: Note) {

    }

    override suspend fun deleteAllNotes() {
    }
}
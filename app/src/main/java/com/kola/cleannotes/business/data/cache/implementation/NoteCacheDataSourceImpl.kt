package com.kola.cleannotes.business.data.cache.implementation

import com.kola.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.kola.cleannotes.business.domain.model.Note
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteCacheDataSourceImpl @Inject constructor(
    private val noteDaoService: NoteDaoService
) : NoteCacheDataSource {
    override suspend fun insertNote(note: Note) = noteDaoService.insertNote(note)

    override suspend fun deleteNote(primaryKey: String) = noteDaoService.deleteNote(primaryKey)

    override suspend fun deleteNotes(notes: List<Note>) = noteDaoService.deleteNotes(notes)

    override suspend fun updateNote(primaryKey: String, newTitle: String, newBody: String) =
        noteDaoService.update(primaryKey, newTitle, newBody)

    override suspend fun searchNote(query: String, filterAndOrder: String, page: Int)= noteDaoService.searchNote(query,filterAndOrder,page)
    override suspend fun searchNoteById(primaryKey: String)=noteDaoService.searchById(primaryKey)

    override suspend fun getNumNotes()=noteDaoService.getNumNotes()

    override suspend fun insertNotes(notes: List<Note>)=noteDaoService.insertNotes(notes)


}
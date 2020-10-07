package com.kola.cleannotes.framework.datasource.cache.implementation

import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.framework.datasource.cache.abstraction.NoteDaoService
import com.kola.cleannotes.framework.datasource.cache.database.NoteDao
import com.kola.cleannotes.framework.datasource.cache.database.returnOrderedQuery
import com.kola.cleannotes.framework.datasource.cache.util.CacheMapper
import com.kola.cleannotes.util.DateUtil
import javax.inject.Inject

class NoteDaoServiceImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val noteMapper: CacheMapper,
    private val dateUtil: DateUtil
) : NoteDaoService {
    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(noteMapper.maToEntity(note))
    }

    override suspend fun deleteNote(primaryKey: String): Int {
        return noteDao.deleteNote(primaryKey)
    }

    override suspend fun deleteNotes(notes: List<Note>): Int {
        val ids = notes.mapIndexed { index, note -> note.id }
        return noteDao.deleteNotes(ids)
    }

    override suspend fun updateNote(primaryKey: String, newTitle: String, newBody: String): Int {
        return noteDao.updateNote(
            primaryKey = primaryKey,
            title = newTitle,
            body = newBody,
            updated_at = dateUtil.getCurrentTimeStamp()
        )
    }

    override suspend fun searchNote(): List<Note> {
        return noteMapper.entityListToNoteList(noteDao.searchNotes())
    }

    override suspend fun getAllNotes(): List<Note> {
        return noteMapper.entityListToNoteList(noteDao.searchNotes())
    }

    override suspend fun searchNotesOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotesOrderByDateDESC(
                query=query,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun searchNotesOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotesOrderByDateASC(
                query=query,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun searchNotesOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotesOrderByTitleDESC(
                query=query,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun searchNotesOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotesOrderByTitleASC(
                query=query,
                page = page,
                pageSize = pageSize
            )
        )
    }

    override suspend fun searchNoteById(primaryKey: String): Note? {
        return noteDao.searchNoteById(primaryKey)?.let { noteCacheEntity ->
            noteMapper.mapFromEntity(noteCacheEntity)

        }
    }

    override suspend fun getNumNotes(): Int {
        return noteDao.getNumNotes()
    }

    override suspend fun insertNotes(notes: List<Note>): LongArray {
        return noteDao.insertNotes(noteMapper.noteListToEntityList(notes))
    }

    override suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.returnOrderedQuery(
                query=query,
                page = page,
                filterAndOrder = filterAndOrder
            )
        )
    }

}
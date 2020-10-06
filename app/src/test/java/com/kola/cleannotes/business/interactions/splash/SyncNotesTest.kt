package com.kola.cleannotes.business.interactions.splash

import com.kola.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.kola.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.business.domain.model.NoteFactory
import com.kola.cleannotes.di.DependencyContainer
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.ArrayList


/*
Test cases:
1. insertNetworkNotesIntoCache()
    a) insert a bunch of new notes into the cache
    b) perform the sync
    c) check to see that those notes were inserted into the network
2. insertCachedNotesIntoNetwork()
    a) insert a bunch of new notes into the network
    b) perform the sync
    c) check to see that those notes were inserted into the cache
3. checkCacheUpdateLogicSync()
    a) select some notes from the cache and update them
    b) perform sync
    c) confirm network reflects the updates
4. checkNetworkUpdateLogicSync()
    a) select some notes from the network and update them
    b) perform sync
    c) confirm cache reflects the updates
 */

class SyncNotesTest {
    // system in test
    private val syncNotes: SyncNotes

    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        syncNotes = SyncNotes(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    @Test
    fun insertNetworkNotesIntoCache() = runBlocking {
        val newNotes = noteFactory.createNoteList(50)
        noteNetworkDataSource.insertOrUpdateNotes(newNotes)

        syncNotes.syncNotes()
        for (note in newNotes) {
            val cachedNote = noteCacheDataSource.searchNoteById(note.id)
            Assertions.assertTrue(cachedNote != null)
        }
    }

    @Test
    fun insertCachedNotesIntoNetwork() = runBlocking {
        val newNotes = noteFactory.createNoteList(50)
        noteCacheDataSource.insertNotes(newNotes)
        syncNotes.syncNotes()

        newNotes.forEach { note ->
            val networkNote = noteNetworkDataSource.searchNote(note)
            assertTrue(networkNote != null)
        }

    }

    @Test
    fun checkCacheUpdateLogicSync() = runBlocking {
        val cachedNotes = noteCacheDataSource.searchNotes("", "", page = 1)
        val notesToUpdate: ArrayList<Note> = ArrayList()
        for (note in cachedNotes) {
            val updatNote = noteFactory.createSingleNote(
                id = note.id,
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString()
            )

            notesToUpdate.add(updatNote)
            if (notesToUpdate.size > 4) {
                break
            }
        }
        noteCacheDataSource.insertNotes(notesToUpdate)
        syncNotes.syncNotes()

        for (note in notesToUpdate) {
            val networkNote = noteNetworkDataSource.searchNote(note)
            assertEquals(note.id, networkNote?.id)
            assertEquals(note.title, networkNote?.title)
            assertEquals(note.body, networkNote?.body)
            assertEquals(note.updated_at, networkNote?.updated_at)
        }
    }

    @Test
    fun checkNetworkUpdateLogicSync() = runBlocking {
        val networkNotes = noteNetworkDataSource.getAllNotes()
        val notesToUpdate: ArrayList<Note> = ArrayList()
        for (note in networkNotes) {
            val updatNote = noteFactory.createSingleNote(
                id = note.id,
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString()
            )

            notesToUpdate.add(updatNote)
            if (notesToUpdate.size > 4) {
                break
            }
        }
        noteNetworkDataSource.insertOrUpdateNotes(notesToUpdate)
        syncNotes.syncNotes()

        for (note in notesToUpdate) {
            val cachedNote = noteCacheDataSource.searchNoteById(note.id)
            assertEquals(note.id, cachedNote?.id)
            assertEquals(note.title, cachedNote?.title)
            assertEquals(note.body, cachedNote?.body)
            assertEquals(note.updated_at, cachedNote?.updated_at)
        }
    }

}
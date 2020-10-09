package com.kola.cleannotes.framework.datasource.data

import android.app.Application
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.business.domain.model.NoteFactory
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteDataFactory @Inject constructor(
    private val application: Application,
    private val noteFactory: NoteFactory
) {
    fun produceListOfNotes(): List<Note> {
        val notes: List<Note> = Gson().fromJson(
            readJSONFromAsset("note_list_json"),
            object : TypeToken<List<Note>>() {}.type
        )
        return notes
    }

    fun produiceEmptyListOfNotes(): List<Note> {
        return ArrayList()
    }

    private fun readJSONFromAsset(fileName: String): String? {
        var json: String? = null
        json = try {
            val inputStream: InputStream = (application.assets as AssetManager).open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return json
    }

    fun createSingleNote(
        id: String? = null,
        title: String,
        body: String? = null
    ) = noteFactory.createSingleNote(id, title, body)

    fun createNoteList(numNotes: Int) = noteFactory.createNoteList(numNotes)
}
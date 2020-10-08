package com.kola.cleannotes.framework.datasource.network.implementation

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kola.cleannotes.business.domain.model.Note
import com.kola.cleannotes.framework.datasource.network.abstraction.NoteFirestoreService
import com.kola.cleannotes.framework.datasource.network.mappers.NetworkMapper
import com.kola.cleannotes.framework.datasource.network.model.NoteNetworkEntity
import com.kola.cleannotes.util.cLog
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteFirestoreServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firstore: FirebaseFirestore,
    private val networkMapper: NetworkMapper
) : NoteFirestoreService {
    override suspend fun insertOrUpdateNote(note: Note) {
        val entity = networkMapper.maToEntity(note)
        entity.created_at = Timestamp.now()
        firstore.collection(NOTES_COLLECTION).document(USER_ID).collection(NOTES_COLLECTION)
            .document(entity.id).set(entity).await()
    }

    override suspend fun insertOrUpdateNotes(notes: List<Note>) {
        if (notes.size > 500) {
            throw Exception("can't insert more than 500 notes at a time into firestore")
        }
        val collectionRef = firstore.collection(NOTES_COLLECTION).document(USER_ID).collection(
            NOTES_COLLECTION
        )

        firstore.runBatch { batch ->
            for (note in notes) {
                val entity = networkMapper.maToEntity(note)
                entity.updated_at = Timestamp.now()
                val documentRef = collectionRef.document(note.id)
                batch.set(documentRef, entity)
            }
        }
    }

    override suspend fun deleteNote(primaryKey: String) {
        firstore.collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(primaryKey)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun insertDeletedNote(note: Note) {
        val entity = networkMapper.maToEntity(note)
        entity.created_at = Timestamp.now()
        firstore.collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun insertDeletedNotes(notes: List<Note>) {
        if (notes.size > 500) {
            throw Exception("can't insert more than 500 notes at a time into firestore")
        }
        val collectionRef = firstore.collection(DELETES_COLLECTION).document(USER_ID).collection(
            NOTES_COLLECTION
        )

        firstore.runBatch { batch ->
            for (note in notes) {
                val entity = networkMapper.maToEntity(note)
                entity.updated_at = Timestamp.now()
                val documentRef = collectionRef.document(note.id)
                batch.set(documentRef, entity)
            }
        }
    }

    override suspend fun deleteDeletedNote(note: Note) {
        firstore.collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
    }

    override suspend fun deleteAllNotes() {
        firstore.collection(DELETES_COLLECTION).document(USER_ID)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
        firstore.collection(NOTES_COLLECTION).document(USER_ID)
            .delete()
            .addOnFailureListener {
                cLog(it.message)
            }
    }

    override suspend fun getDeletedNotes(): List<Note> {
        return networkMapper.entityListToNoteList(
            firstore.collection(DELETES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await().toObjects(NoteNetworkEntity::class.java)
        )
    }

    override suspend fun searchNote(note: Note): Note? {
        return firstore
            .collection(NOTES_COLLECTION).document(USER_ID).collection(NOTES_COLLECTION)
            .document(note.id)
            .get()
            .addOnFailureListener {
                cLog(it.message)
            }.await().toObject(NoteNetworkEntity::class.java)?.let {
                networkMapper.mapFromEntity(it)
            }
    }

    override suspend fun getAllNotes(): List<Note> {
        return networkMapper.entityListToNoteList(
            firstore.collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await().toObjects(NoteNetworkEntity::class.java)
        )
    }

    companion object {
        const val NOTES_COLLECTION = "notes"
        const val USERS_COLLECTION = "users"
        const val DELETES_COLLECTION = "deletes"
        const val USER_ID = "3fGWcCaD5gSsqqOmzm0Z4eI1F8Z2" // hardcoded for single user
        const val EMAIL = "mitch@tabian.ca"
    }

}
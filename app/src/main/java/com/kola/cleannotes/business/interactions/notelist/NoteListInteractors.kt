package com.kola.cleannotes.business.interactions.notelist

import com.kola.cleannotes.business.interactions.common.DeleteNote
import com.kola.cleannotes.framework.presentation.notelist.state.NoteListViewState


// Use cases
class NoteListInteractors (
    val insertNewNote: InsertNewNote,
    val deleteNote: DeleteNote<NoteListViewState>,
    val searchNotes: SearchNote,
    val getNumNotes: GetNumNotes,
    val restoreDeletedNote: RestoreDeletedNote,
    val deleteMultipleNotes: DeleteMultipleNotes
)
package com.kola.cleannotes.business.interactions.notedetail

import com.kola.cleannotes.business.interactions.common.DeleteNote
import com.kola.cleannotes.framework.presentation.notedetail.state.NoteDetailViewState

// Use cases
class NoteDetailInteractors (
    val deleteNote: DeleteNote<NoteDetailViewState>,
    val updateNote: UpdateNote
)
import NoteRepository from "../repositories/NoteRepository.js";

export default {
  archive,
  deleteNote
};

async function archive(note) {
  note.archived = !note.archived;
  return await NoteRepository.update(note);
}

async function deleteNote(noteId) {
  return await NoteRepository.delete(noteId);
}

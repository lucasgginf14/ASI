import NoteList from "./NoteList.vue";
import NoteDetail from "./NoteDetail.vue";
import NoteForm from "./NoteForm.vue";

export default [
  {
    path: "/notes",
    name: "NoteList",
    component: NoteList
  },
  {
    path: "/notes/:noteId",
    name: "NoteDetail",
    component: NoteDetail
  },
  {
    path: "/notes/create",
    name: "CreateNote",
    component: NoteForm,
    meta: { authority: "USER" }
  },
  {
    path: "/notes/:noteId/edit",
    name: "EditNote",
    component: NoteForm
  }
];

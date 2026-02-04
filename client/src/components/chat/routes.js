import ChatView from "./ChatView.vue";

export default [
  {
    path: "/chats",
    name: "ChatView",
    component: ChatView,
    meta: { requiresAuth: true }
  }
];

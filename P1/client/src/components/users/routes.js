import UserList from "./UserList.vue";

export default [
  {
    path: "/users",
    name: "UserList",
    component: UserList,
    meta: { authority: "ADMIN" }
  }
];

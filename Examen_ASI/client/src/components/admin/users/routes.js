import AdminUserList from "./AdminUserList.vue";
import AdminUserDetail from "./AdminUserDetail.vue";

export default [
  {
    path: "/admin/users",
    name: "AdminUserList",
    component: AdminUserList,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/admin/users/:id",
    name: "AdminUserDetail",
    component: AdminUserDetail,
    meta: { authority: "ADMIN" }
  }
];

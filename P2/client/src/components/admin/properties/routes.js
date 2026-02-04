import AdminPropertyList from "./AdminPropertyList.vue";
import AdminPropertyDetail from "./AdminPropertyDetail.vue";

export default [
  {
    path: "/admin/properties",
    name: "AdminPropertyList",
    component: AdminPropertyList,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/admin/properties/:id",
    name: "AdminPropertyDetail",
    component: AdminPropertyDetail,
    meta: { authority: "ADMIN" }
  }
];

import CategoryDetail from "./CategoryDetail.vue";
import CategoryList from "./CategoryList.vue";
import CategoryForm from "./CategoryForm.vue";

export default [
  {
    path: "/categories",
    name: "CategoryList",
    component: CategoryList,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/categories/:categoryId",
    name: "CategoryDetail",
    component: CategoryDetail,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/categories/create",
    name: "CategoryForm",
    component: CategoryForm,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/categories/:categoryId/edit",
    name: "EditCategory",
    component: CategoryForm,
    meta: { authority: "ADMIN" }
  }
];

import AdminReviewList from "./AdminReviewList.vue";

export default [
  {
    path: "/admin/reviews",
    name: "AdminReviewList",
    component: AdminReviewList,
    meta: { authority: "ADMIN" }
  }
];

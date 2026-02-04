import PendingReviews from "./PendingReviews.vue";

export default [
  {
    path: "/pending-reviews",
    name: "MyPendingReviews",
    component: PendingReviews,
    meta: { requiresAuth: true }
  }
];

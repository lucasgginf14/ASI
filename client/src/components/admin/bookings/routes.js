import AdminBookingList from "./AdminBookingList.vue";
import AdminBookingDetail from "./AdminBookingDetail.vue";

export default [
  {
    path: "/admin/bookings",
    name: "AdminBookingsList",
    component: AdminBookingList,
    meta: { authority: "ADMIN" }
  },
  {
    path: "/admin/bookings/:id",
    name: "AdminBookingDetails",
    component: AdminBookingDetail,
    meta: { authority: "ADMIN" }
  }
];

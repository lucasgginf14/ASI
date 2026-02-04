import HostIncomingRequests from "./HostIncomingRequests.vue";
import HostReservationsList from "./HostReservationsList.vue";
import HostBookingDetail from "./HostBookingDetail.vue";

export default [
  {
    path: "/reservation-requests",
    name: "HostIncomingRequests",
    component: HostIncomingRequests,
    meta: { authority: "USER" }
  },
  {
    path: "/my-reservations-host",
    name: "HostReservationsList",
    component: HostReservationsList,
    meta: { authority: "USER" }
  },
  {
    path: "/my-reservations-host/:id",
    name: "HostBookingDetail",
    component: HostBookingDetail,
    meta: { authority: "USER" }
  }
];

import GuestRequestsList from "./GuestRequestsList.vue";
import GuestTripsList from "./GuestTripsList.vue";
import GuestBookingDetail from "./GuestBookingDetail.vue";
import GuestBookingsList from "@/components/bookings/guest/GuestBookingsList.vue";

export default [
  {
    path: "/my-requests",
    name: "GuestRequests",
    component: GuestRequestsList,
    meta: {
      authority: "USER"
    }
  },
  {
    path: "/my-bookings",
    name: "GuestTrips",
    component: GuestTripsList,
    meta: {
      authority: "USER"
    }
  },
  {
    path: "/my-bookings/:id",
    name: "GuestBookingDetail",
    component: GuestBookingDetail,
    meta: { authority: "USER" }
  },
  {
    path: "/my-bookings/all",
    name: "GuestAllBookings",
    component: GuestBookingsList,
    meta: { authority: "USER" }
  }
];

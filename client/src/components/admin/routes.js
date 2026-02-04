import AdminDashboard from "./AdminDashboard.vue";

// Importamos las rutas de cada módulo
import propertyRoutes from "./properties/routes";
import userRoutes from "./users/routes";
import reservationRoutes from "@/components/admin/bookings/routes";
import reviewRoutes from "./reviews/routes";
import auth from "@/common/auth.js";

// Definimos la ruta base del Dashboard
const dashboardRoutes = [
  {
    path: "/admin",
    name: "AdminDashboard",
    component: AdminDashboard,
    meta: { authority: "ADMIN" },
    beforeEnter: (to, from, next) => {
      if (auth.isAdmin()) {
        next();
      } else {
        next({ path: "/" });
      }
    }
  }
];

const adminGuard = (to, from, next) => {
  if (auth.isAdmin()) {
    next();
  } else {
    next({ path: "/" });
  }
};

const protectRoutes = (routes) =>
  routes.map((r) => {
    r.meta = Object.assign({}, r.meta, { authority: "ADMIN" });
    if (!r.beforeEnter) r.beforeEnter = adminGuard;
    return r;
  });

export default [
  ...dashboardRoutes,
  ...protectRoutes(propertyRoutes),
  ...protectRoutes(userRoutes),
  ...protectRoutes(reservationRoutes),
  ...protectRoutes(reviewRoutes)
];

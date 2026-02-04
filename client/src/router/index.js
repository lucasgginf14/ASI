import { createRouter, createWebHistory } from "vue-router";
import AboutView from "../views/AboutView.vue";
import ErrorNotFoundView from "../views/ErrorNotFoundView.vue";

import auth from "@/common/auth";
import { getStore } from "@/common/store";

import homeRoutes from "@/components/home/routes.js";
import accountRoutes from "@/components/account/routes.js";
import profileRoutes from "@/components/profile/routes.js";
import propertyRoutes from "@/components/properties/routes.js";
import adminRoutes from "@/components/admin/routes.js";
import reviewRoutes from "@/components/reviews/routes.js";
import chatRoutes from "@/components/chat/routes.js";
import searchRoutes from "@/components/search/routes.js";
import bookingsRoutes from "@/components/bookings/routes.js";

const routes = [
  {
    path: "/about",
    name: "About",
    component: AboutView,
    meta: { public: true }
  },
  {
    path: "/:catchAll(.*)*",
    component: ErrorNotFoundView,
    meta: { public: true }
  }
]
  .concat(homeRoutes)
  .concat(accountRoutes)
  .concat(profileRoutes)
  .concat(propertyRoutes)
  .concat(adminRoutes)
  .concat(reviewRoutes)
  .concat(chatRoutes)
  .concat(searchRoutes)
  .concat(bookingsRoutes);

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

router.beforeEach((to, from, next) => {
  auth.isAuthenticationChecked.finally(() => {
    const requiresAuth = !to.meta.public;
    const userIsLogged = getStore().state.user.logged;

    if (requiresAuth) {
      if (userIsLogged) {
        next();
      } else {
        alert("Esta página requiere autenticación");
        next("/login");
      }
    } else {
      if (userIsLogged && to.meta.isLoginPage) {
        next({ name: "Home", replace: true });
      } else {
        next();
      }
    }
  });
});

export default router;

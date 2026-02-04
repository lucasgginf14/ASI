import HomeView from "./HomeView.vue";
import PropertyDetailPublic from "../properties/PropertyDetailPublic.vue";

export default [
  {
    path: "/",
    name: "Home",
    component: HomeView,
    meta: { public: true } // Importante: Permite acceso sin login
  },
  {
    path: "/properties/:id",
    name: "PropertyDetailPublic",
    component: PropertyDetailPublic,
    props: true,
    meta: { public: true } // Cualquiera puede ver el detalle
  }
];

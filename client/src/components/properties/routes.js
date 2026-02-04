import MyProperties from "./MyProperties.vue";
import PropertyForm from "./PropertyForm.vue";
import PropertyOwnerDetail from "@/components/properties/PropertyOwnerDetail.vue";

export default [
  {
    path: "/my-properties",
    name: "MyProperties",
    component: MyProperties
  },
  {
    path: "/properties/create",
    name: "PropertyCreate",
    component: PropertyForm
  },
  {
    path: "/my-properties/:id",
    name: "PropertyOwnerDetail",
    component: PropertyOwnerDetail,
    props: true
  },
  {
    path: "/my-properties/:id/edit",
    name: "PropertyEdit",
    component: PropertyForm,
    props: true
  }
];

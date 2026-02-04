import ProfileView from "./ProfileView.vue";
import ProfileEdit from "./ProfileEdit.vue";
import PasswordChange from "./PasswordChange.vue";
import PublicProfileView from "./PublicProfileView.vue";

export default [
  {
    path: "/profile",
    name: "ProfileView",
    component: ProfileView
  },
  {
    path: "/profile/edit",
    name: "ProfileEdit",
    component: ProfileEdit
  },
  {
    path: "/profile/password",
    name: "PasswordChange",
    component: PasswordChange
  },
  {
    path: "/users/:id",
    name: "PublicProfileView",
    component: PublicProfileView
  }
];

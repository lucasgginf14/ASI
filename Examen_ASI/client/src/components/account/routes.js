import LoginForm from "./LoginForm.vue";
import RegisterForm from "./RegisterForm.vue";

export default [
  {
    path: "/login",
    name: "Login",
    component: LoginForm,
    meta: { public: true, isLoginPage: true }
  },
  {
    path: "/register",
    name: "Register",
    component: RegisterForm,
    meta: { public: true, isLoginPage: true }
  }
];

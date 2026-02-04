import "bootstrap/dist/css/bootstrap.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

import VCalendar from "v-calendar";
import "v-calendar/style.css";

const app = createApp(App);

app.use(router);
app.use(VCalendar, {});

app.mount("#app");

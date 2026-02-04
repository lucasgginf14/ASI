<template>
  <nav class="navbar">
    <div class="navbar-brand" v-if="store.state.user.authority !== 'ADMIN'">
      <router-link to="/">
        <span class="brand-text">Bnbria</span>
        <img :src="logo" alt="Logo" class="brand-logo" />
      </router-link>
    </div>
    <div v-else class="navbar-brand">
      <router-link to="/admin">
        <span class="brand-text">Bnbria</span>
        <img :src="logo" alt="Logo" class="brand-logo" />
      </router-link>
    </div>

    <div class="navbar-menu">
      <template
        v-if="!store.state.user.logged && $route.path !== '/login' && $route.path !== '/register'"
      >
        <div
          class="auth-actions"
          role="navigation"
          aria-label="Acciones de autenticación"
          style="display: flex; gap: 10px; align-items: center"
        >
          <router-link
            to="/login"
            class="nav-item btn btn-primary"
            aria-label="Iniciar sesión"
            style="
              padding: 8px 12px;
              border-radius: 20px;
              background: linear-gradient(90deg, #0077c8, #66c4ff);
              color: #fff;
              text-decoration: none;
              font-weight: 600;
              display: inline-flex;
              align-items: center;
              gap: 8px;
            "
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
              <path
                d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"
                stroke="currentColor"
                stroke-width="1.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M10 17L15 12 10 7"
                stroke="currentColor"
                stroke-width="1.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M15 12H3"
                stroke="currentColor"
                stroke-width="1.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
            Iniciar Sesión
          </router-link>

          <router-link
            to="/register"
            class="nav-item btn btn-outline"
            aria-label="Registrarse"
            style="
              padding: 8px 12px;
              border-radius: 20px;
              border: 1px solid #0077c8;
              color: #0077c8;
              text-decoration: none;
              font-weight: 600;
              display: inline-flex;
              align-items: center;
              gap: 8px;
              background: transparent;
            "
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true">
              <path
                d="M12 5v14"
                stroke="currentColor"
                stroke-width="1.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
              <path
                d="M5 12h14"
                stroke="currentColor"
                stroke-width="1.5"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
            Registrarse
          </router-link>
        </div>
      </template>
      <template
        v-if="
          store.state.user.logged &&
          $route.path !== '/login' &&
          $route.path !== '/register' &&
          store.state.user.authority !== 'ADMIN'
        "
      >
        <router-link
          to="/chats"
          class="nav-item link-simple position-relative"
          v-if="store.state.user.authority"
          aria-label="Chats"
          style="text-decoration: none"
        >
          <svg
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            style="margin-right: 8px; vertical-align: middle"
          >
            <path
              d="M21 15a2 2 0 0 1-2 2H7l-5 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"
              stroke="currentColor"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
          Chats
          <span
            v-if="userInfo && userInfo.pendingMessages > 0"
            class="badge"
            :title="
              userInfo.pendingMessages +
              (userInfo.pendingMessages === 1
                ? ' conversación sin leer'
                : ' conversaciones sin leer')
            "
            style="
              display: inline-flex;
              align-items: center;
              justify-content: center;
              min-width: 18px;
              height: 18px;
              padding: 0 6px;
              background: #dc3545;
              color: #fff;
              border-radius: 999px;
              font-size: 11px;
              margin-left: 8px;
            "
            aria-hidden="false"
            :aria-label="`${userInfo.pendingMessages} mensajes sin leer`"
          >
            {{ userInfo.pendingMessages }}
          </span>
        </router-link>

        <div>
          <div
            v-if="userInfo && userInfo.hasProperties"
            class="nav-item dropdown"
            role="menu"
            aria-haspopup="true"
          >
            <button class="dropdown-toggle" aria-expanded="false">
              Mis propiedades
              <span
                v-if="userInfo && userInfo.pendingBookings > 0"
                class="badge"
                :title="
                  userInfo.pendingBookings +
                  (userInfo.pendingBookings === 1 ? ' reserva pendiente' : ' reservas pendientes')
                "
                style="
                  display: inline-flex;
                  align-items: center;
                  justify-content: center;
                  min-width: 18px;
                  height: 18px;
                  background: #dc3545;
                  color: #fff;
                  border-radius: 999px;
                  font-size: 11px;
                  margin-left: 8px;
                "
              ></span>
            </button>
            <div class="dropdown-menu" role="menu" aria-label="Menu Mis propiedades">
              <router-link to="/properties/create" class="dropdown-item" role="menuitem"
                >Crear propiedad</router-link
              >
              <router-link to="/my-properties" class="dropdown-item" role="menuitem"
                >Ver mis propiedades</router-link
              >
              <router-link to="/reservation-requests" class="dropdown-item" role="menuitem">
                Ver solicitudes de reservas
                <span
                  v-if="userInfo && userInfo.pendingBookings > 0"
                  class="badge"
                  :title="
                    userInfo.pendingBookings +
                    (userInfo.pendingBookings === 1 ? ' reserva pendiente' : ' reservas pendientes')
                  "
                  style="
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                    min-width: 18px;
                    height: 18px;
                    background: #dc3545;
                    color: #fff;
                    border-radius: 999px;
                    font-size: 11px;
                    margin-left: 8px;
                  "
                >
                  {{ userInfo.pendingBookings }}
                </span>
              </router-link>
              <router-link to="/my-reservations-host" class="dropdown-item" role="menuitem"
                >Ver mis reservas</router-link
              >
            </div>
          </div>

          <router-link
            v-else-if="userInfo"
            to="/properties/create"
            class="dropdown-item"
            role="menuitem"
          >
            Conviértete en anfitrión
          </router-link>
        </div>

        <div class="nav-item dropdown" role="menu" aria-haspopup="true">
          <button class="dropdown-toggle" aria-expanded="false">Mis reservas</button>
          <div class="dropdown-menu" role="menu" aria-label="Menu Mis reservas">
            <router-link to="/my-requests" class="dropdown-item" role="menuitem"
              >Ver mis solicitudes</router-link
            >
            <router-link to="/my-bookings" class="dropdown-item" role="menuitem"
              >Mis Viajes</router-link
            >
            <router-link to="/my-bookings/all" class="dropdown-item" role="menuitem"
              >Todas mis reservas</router-link
            >
          </div>
        </div>

        <div class="nav-item dropdown" role="menu" aria-haspopup="true">
          <button class="dropdown-toggle" aria-expanded="false">
            Mi Perfil
            <span
              v-if="userInfo && userInfo.pendingPropertyReviews + userInfo.pendingUserReviews > 0"
              class="badge"
              :title="
                userInfo.pendingPropertyReviews +
                userInfo.pendingUserReviews +
                (userInfo.pendingPropertyReviews + userInfo.pendingUserReviews === 1
                  ? ' reseña pendiente'
                  : ' reseñas pendientes')
              "
              style="
                display: inline-flex;
                align-items: center;
                justify-content: center;
                min-width: 18px;
                height: 18px;
                background: #dc3545;
                color: #fff;
                border-radius: 999px;
                font-size: 11px;
                margin-left: 8px;
              "
            ></span>
          </button>
          <div class="dropdown-menu right-aligned" role="menu" aria-label="Menu Mi Perfil">
            <router-link to="/profile" class="dropdown-item" role="menuitem"
              >Ver mi perfil</router-link
            >
            <router-link to="/pending-reviews" class="dropdown-item" role="menuitem">
              Reseñas Pendientes
              <span
                v-if="userInfo && userInfo.pendingPropertyReviews + userInfo.pendingUserReviews > 0"
                class="badge"
                :title="
                  userInfo.pendingPropertyReviews +
                  userInfo.pendingUserReviews +
                  (userInfo.pendingPropertyReviews + userInfo.pendingUserReviews === 1
                    ? ' reseña pendiente'
                    : ' reseñas pendientes')
                "
                style="
                  display: inline-flex;
                  align-items: center;
                  justify-content: center;
                  min-width: 18px;
                  height: 18px;
                  background: #dc3545;
                  color: #fff;
                  border-radius: 999px;
                  font-size: 11px;
                  margin-left: 8px;
                "
              >
                {{ userInfo.pendingPropertyReviews + userInfo.pendingUserReviews }}
              </span>
            </router-link>
            <router-link to="/profile/edit" class="dropdown-item" role="menuitem"
              >Editar mi perfil</router-link
            >
            <router-link to="/profile/password" class="dropdown-item" role="menuitem"
              >Cambiar contraseña</router-link
            >
            <a class="dropdown-item" @click="handleLogout" role="menuitem">Cerrar sesión</a>
          </div>
        </div>

        <div
          class="profile-name"
          style="display: flex; align-items: center; gap: 12px; padding-left: 12px"
        >
          <div v-if="userInfo" style="display: flex; align-items: center; gap: 10px">
            <div style="line-height: 1.1">
              <div style="font-size: 0.95rem">
                <strong>Bienvenido,</strong>
                <br />{{ userInfo.name ?? "" }}
              </div>
            </div>
          </div>
        </div>
      </template>

      <template
        v-if="
          store.state.user.logged &&
          $route.path !== '/login' &&
          $route.path !== '/register' &&
          store.state.user.authority === 'ADMIN'
        "
      >
        <div>
          <button @click="handleLogout" class="btn-logout" aria-label="Cerrar sesión">
            <svg
              class="btn-icon"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              width="16"
              height="16"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              style="margin-right: 8px"
            >
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
              <polyline points="16 17 21 12 16 7"></polyline>
              <line x1="21" y1="12" x2="9" y2="12"></line>
            </svg>
            Cerrar sesión
          </button>
        </div>
      </template>
    </div>
  </nav>

  <router-view />
</template>

<script>
import { getStore } from "./common/store";
import auth from "./common/auth";
import logoImg from "@/assets/logo.png";

export default {
  name: "App",
  data() {
    return {
      store: getStore(),
      logo: logoImg,
      userInfo: null
    };
  },
  async created() {
    try {
      if (this.store.state.user.logged) {
        await this.fetchUserInfo();
      }
    } catch (e) {
      console.error("Error fetching user info", e);
    }
  },
  watch: {
    "store.state.user.logged"(val) {
      if (val) {
        this.fetchUserInfo();
      } else {
        this.userInfo = null;
      }
    },
    "$route.path"() {
      if (this.store.state.user.logged) {
        this.fetchUserInfo();
      }
    }
  },
  methods: {
    async fetchUserInfo() {
      try {
        this.userInfo = await auth.getAccountInfo();
      } catch (e) {
        console.error("Error fetching user info", e);
      }
    },
    formatDate(date) {
      if (!date) return "-";
      return new Date(date).toLocaleDateString("es-ES", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
      });
    },
    handleLogout() {
      auth.logout();
      this.$router.push("/");
    }
  }
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  color: #333;
  background-color: #fff;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
  height: 70px;
  background-color: #fff;
  border-bottom: 1px solid #ccc;
}

.navbar-brand a {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #000;
}

.brand-text {
  font-size: 1.8rem;
  font-weight: bold;
  letter-spacing: -0.5px;
}

.brand-logo {
  height: 30px;
  width: auto;
}

.navbar-menu {
  display: flex;
  height: 100%;
  align-items: center;
}

.nav-item {
  height: 50%;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-left: 1px solid #ccc;
  font-size: 1rem;
  color: #333;
  position: relative;
}

.navbar-menu .nav-item:first-child {
  border-left: none;
}

.nav-item:last-child {
  border-right: 0;
}

.link-simple {
  text-decoration: none;
  color: #333;
  font-weight: 500;
}
.link-simple:hover {
  text-decoration: underline;
}

.dropdown {
  cursor: pointer;
}

.dropdown-toggle {
  background: none;
  border: none;
  font-size: 1rem;
  font-family: inherit;
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 0;
}

.dropdown-menu {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  background: #fff;
  border: 1px solid #ccc;
  min-width: 200px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 0;
}

.dropdown-menu.right-aligned {
  left: auto;
  right: 0;
}

.dropdown:hover .dropdown-menu {
  display: block;
}

.dropdown-item {
  display: block;
  padding: 12px 15px;
  text-decoration: none;
  color: #333;
  border-bottom: 1px solid #eee;
  font-size: 0.95rem;
  background: #fff;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.btn-logout {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(90deg, #0077c8, #66c4ff);
  color: #fff;
  border: none;
  padding: 8px 12px;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
  transition:
    transform 0.12s ease,
    box-shadow 0.12s ease,
    opacity 0.12s;
}
.btn-logout:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.14);
  opacity: 0.98;
}
.btn-logout:active {
  transform: translateY(0);
}
.btn-logout:focus {
  outline: 2px solid rgba(255, 95, 109, 0.18);
  outline-offset: 2px;
}
.btn-icon {
  display: inline-block;
  vertical-align: middle;
}
</style>

<template>
  <div class="booking-detail">
    <back-button />

    <div v-if="loading" class="loader">
      <div class="shimmer" />
      <div class="loader-text">Cargando reserva...</div>
    </div>

    <div v-else class="card">
      <div v-if="error" class="error">{{ error }}</div>

      <header class="card-header">
        <div class="header-left">
          <h1 class="title">Reserva #{{ booking.id }}</h1>
          <div class="sub">
            <small class="muted">Solicitada</small>
            <div class="requested">{{ formatDateTime(booking.requestDate) }}</div>
          </div>
        </div>

        <div class="header-right">
          <span :class="stateClass(booking.state)">{{ translateState(booking.state) }}</span>
        </div>
      </header>

      <section class="info-grid">
        <div class="info-row">
          <span class="label">Fecha inicio</span>
          <span class="value">{{ formatDate(booking.startDate) }}</span>
        </div>
        <div class="info-row">
          <span class="label">Fecha fin</span>
          <span class="value">{{ formatDate(booking.endDate) }}</span>
        </div>
        <div class="info-row">
          <span class="label">Precio</span>
          <span class="value">{{ booking.price ?? "-" }}</span>
        </div>
        <div class="info-row">
          <span class="label">Nº de huéspedes</span>
          <span class="value">{{ booking.numGuests ?? "-" }}</span>
        </div>
      </section>

      <section class="request-text">
        <h3>Texto de solicitud</h3>
        <p>{{ booking.requestText ?? "" }}</p>
      </section>

      <p v-if="isCancelled && booking.cancellationReason" class="cancellation">
        <strong>Razón de cancelación:</strong> {{ booking.cancellationReason }}
      </p>

      <div class="relations">
        <div class="relation-card">
          <h4>Huésped</h4>
          <div class="relation-body">
            <button v-if="booking.guest" class="link-btn" @click="goToUser(booking.guest.id)">
              {{ booking.guest.name }} {{ booking.guest.surname }}
              {{ booking.guest.surname2 || "" }}
            </button>
            <div class="meta">Usuario desde: {{ formatDate(booking.guest?.creationDate) }}</div>
          </div>
        </div>

        <div class="relation-card">
          <h4>Propietario</h4>
          <div class="relation-body">
            <button v-if="booking.owner" class="link-btn" @click="goToUser(booking.owner.id)">
              {{ booking.owner.name }} {{ booking.owner.surname }}
              {{ booking.owner.surname2 || "" }}
            </button>
            <div class="meta">Usuario desde: {{ formatDate(booking.owner?.creationDate) }}</div>
          </div>
        </div>

        <div v-if="booking.property" class="relation-card">
          <h4>Propiedad</h4>
          <div class="relation-body">
            <button class="link-btn" @click="goToProperty(booking.property.id)">
              {{ booking.property.title }}
            </button>
            <div class="meta">
              Ubicación: {{ booking.property.city }}, {{ booking.property.country }}
            </div>
          </div>
        </div>
      </div>

      <div v-if="!isCancelled" class="actions">
        <button
          class="btn btn-danger"
          @click="confirmCancel"
          :disabled="cancelling || booking.state === 'CANCELLED'"
        >
          {{ cancelling ? "Cancelando..." : "Cancelar reserva" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import BookingRepository from "@/repositories/BookingRepository";
import BackButton from "@/common/utils/BackButton.vue";

const route = useRoute();
const router = useRouter();
const id = route.params.id;

const booking = ref(null);
const loading = ref(true);
const error = ref(null);
const cancelling = ref(false);

function formatDate(d) {
  if (!d) return "-";
  const date = new Date(d);
  return new Intl.DateTimeFormat("es-ES", {
    day: "2-digit",
    month: "short",
    year: "numeric"
  }).format(date);
}
function formatDateTime(d) {
  if (!d) return "-";
  const date = new Date(d);
  return new Intl.DateTimeFormat("es-ES", { dateStyle: "short", timeStyle: "short" }).format(date);
}

function isCancelled() {
  return [
    "CANCELLED",
    "REJECTED",
    "CANCELED_BY_GUEST",
    "CANCELED_BY_HOST",
    "CANCELED_BY_SYSTEM",
    "CANCELED_BY_ADMIN"
  ].includes(this.booking?.state);
}

async function load() {
  loading.value = true;
  error.value = null;
  try {
    booking.value = await BookingRepository.getBookingDetailsAdmin(id);
  } catch (e) {
    error.value = "No se pudo cargar la reserva.";
    console.error(e);
  } finally {
    loading.value = false;
  }
}

function goToUser(userId) {
  if (!userId) return;
  router.push(`/admin/users/${userId}`);
}

function goToProperty(propId) {
  if (!propId) return;
  router.push(`/admin/properties/${propId}`);
}

async function confirmCancel() {
  if (!booking.value) return;
  const reason = window.prompt("Introduce la razón de la cancelación (obligatorio):");
  if (!reason || !reason.trim()) {
    alert("La razón es obligatoria.");
    return;
  }
  cancelling.value = true;
  try {
    const token = localStorage.getItem("token");
    await BookingRepository.cancelBooking(booking.value.id, reason, token);
    await load();
    alert("Reserva cancelada correctamente.");
  } catch (e) {
    console.error(e);
    alert("Error al cancelar la reserva.");
  } finally {
    cancelling.value = false;
  }
}

function stateClass(s) {
  const base = "state";
  if (!s) return base;
  const key = String(s).toUpperCase();
  if (key.includes("CANCELED")) return `${base} cancelled`;
  if (key === "CONFIRMED") return `${base} confirmed`;
  if (key === "PENDING") return `${base} pending`;
  if (key === "IN_PROGRESS") return `${base} in-progress`;
  if (key === "COMPLETED") return `${base} completed`;
  return base;
}

function translateState(s) {
  if (!s) return "-";
  switch (String(s).toUpperCase()) {
    case "PENDING":
      return "Pendiente";
    case "CONFIRMED":
      return "Confirmada";
    case "CANCELED_BY_GUEST":
      return "Cancelada por huésped";
    case "CANCELED_BY_HOST":
      return "Cancelada por anfitrión";
    case "CANCELED_BY_ADMIN":
      return "Cancelada por administrador";
    case "CANCELED_BY_SYSTEM":
      return "Cancelada";
    case "IN_PROGRESS":
      return "En curso";
    case "COMPLETED":
      return "Completada";
    default:
      return String(s)
        .toLowerCase()
        .split("_")
        .map((p) => p.charAt(0).toUpperCase() + p.slice(1))
        .join(" ");
  }
}

onMounted(load);
</script>

<style scoped>
:root {
  --bg: #f6f8fb;
  --card-bg: #ffffff;
  --muted: #6b7280;
  --accent: #2563eb;
  --accent-2: #0b608d;
  --danger: #b00020;
  --glass: rgba(37, 99, 235, 0.04);
  --border: #e6eefc;
  --radius: 12px;
  --shadow-1: 0 6px 20px rgba(15, 23, 42, 0.06);
  --shadow-2: 0 10px 30px rgba(2, 6, 23, 0.06);
  --max-width: 980px;
  font-synthesis: none;
}

.booking-detail {
  max-width: var(--max-width);
  margin: 2rem auto;
  padding: 0 1rem;
  font-family:
    Inter,
    system-ui,
    -apple-system,
    "Segoe UI",
    Roboto,
    "Helvetica Neue",
    Arial;
  color: #0f172a;
  background: linear-gradient(180deg, transparent 0%, rgba(2, 6, 23, 0.02) 100%);
}

/* Loader */
.loader {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: linear-gradient(90deg, #f8fbff, #ffffff);
  border: 1px solid var(--border);
  padding: 1rem;
  border-radius: var(--radius);
  box-shadow: var(--shadow-1);
}
.loader .shimmer {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: linear-gradient(90deg, #eef6ff, #f7fbff, #eef6ff);
  animation: shimmer 1.4s infinite;
}
@keyframes shimmer {
  0% {
    background-position: -120px 0;
  }
  100% {
    background-position: 120px 0;
  }
}
.loader-text {
  color: var(--muted);
  font-weight: 600;
}

/* Card */
.card {
  margin-top: 1rem;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.9), var(--card-bg));
  border: 1px solid var(--border);
  padding: 1.5rem;
  border-radius: calc(var(--radius) + 4px);
  box-shadow: var(--shadow-2);
}

/* Header */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 0.9rem;
  border-bottom: 1px dashed rgba(2, 6, 23, 0.04);
  padding-bottom: 0.9rem;
}
.header-left {
  display: flex;
  gap: 1rem;
  align-items: center;
}
.title {
  margin: 0;
  font-size: 1.25rem;
  color: #082244;
  letter-spacing: -0.2px;
}
.sub {
  display: flex;
  flex-direction: column;
  color: var(--muted);
}
.sub .requested {
  font-weight: 600;
  color: #0f172a;
  font-size: 0.9rem;
}

/* Grid info */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
  gap: 0.75rem 1rem;
  margin: 1rem 0 1.15rem 0;
}
.info-row {
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, rgba(37, 99, 235, 0.03), rgba(255, 255, 255, 0.6));
  padding: 0.8rem;
  border-radius: 10px;
  border: 1px solid transparent;
  min-height: 58px;
}
.label {
  font-size: 0.75rem;
  color: var(--muted);
  margin-bottom: 0.25rem;
}
.value {
  font-weight: 700;
  color: #06263b;
}

/* Request text */
.request-text {
  margin-top: 0.75rem;
  padding: 0.9rem;
  background: linear-gradient(180deg, rgba(236, 249, 255, 0.6), rgba(245, 252, 255, 0.5));
  border-radius: 10px;
  border: 1px solid var(--border);
}
.request-text h3 {
  margin: 0;
  font-size: 1rem;
  color: #082244;
}
.request-text p {
  margin: 0.5rem 0 0 0;
  color: #0f172a;
  line-height: 1.35;
}

/* Cancellation reason */
.cancellation {
  margin-top: 0.75rem;
  color: #4b5563;
  background: rgba(255, 250, 250, 0.9);
  padding: 0.6rem;
  border-radius: 8px;
  border: 1px solid rgba(176, 0, 32, 0.04);
}

/* Relations */
.relations {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}
.relation-card {
  background: rgba(255, 255, 255, 0.6);
  padding: 0.85rem;
  border-radius: 10px;
  border: 1px solid var(--border);
}
.relation-card h4 {
  margin: 0 0 0.45rem 0;
  font-size: 0.95rem;
  color: #082244;
}
.relation-body {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}
.meta {
  color: var(--muted);
  font-size: 0.85rem;
}

/* Buttons */
.btn {
  padding: 0.6rem 0.95rem;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-weight: 700;
  transition:
    transform 0.08s ease,
    box-shadow 0.12s ease,
    opacity 0.12s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
}
.btn:active {
  transform: translateY(1px);
}
.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

/* Primary replaced by ghost and danger variants */
.btn-danger {
  background: linear-gradient(180deg, #ff6b6b, #e04b4b);
  color: white;
  box-shadow: 0 8px 22px rgba(224, 75, 75, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.06);
}
.btn-danger:disabled {
  background: linear-gradient(180deg, #f3c7c7, #eec5c5);
  color: #8b3b3b;
  box-shadow: none;
}

/* link-like buttons */
.link-btn {
  background: transparent;
  border: 1px dashed rgba(37, 99, 235, 0.12);
  color: var(--accent-2);
  padding: 0.45rem 0.6rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 700;
  text-align: left;
}
.link-btn:hover {
  background: rgba(37, 99, 235, 0.04);
  transform: translateY(-1px);
}

/* Error */
.error {
  color: var(--danger);
  margin-bottom: 1rem;
  padding: 0.65rem;
  border-radius: 10px;
  background: rgba(176, 0, 32, 0.04);
  border: 1px solid rgba(176, 0, 32, 0.06);
  font-weight: 700;
}

/* Responsive */
@media (max-width: 720px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.6rem;
  }
  .relations {
    grid-template-columns: 1fr;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<template>
  <div class="mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="fw-bold">Disponibilidad</h4>
      <button
        v-if="!showForm && isApproved"
        @click="openNewForm"
        class="btn btn-black rounded-pill px-3 py-2 text-sm"
      >
        + Añadir fechas
      </button>
    </div>

    <div v-if="!isApproved" class="alert alert-light border border-warning text-warning small">
      <i class="bi bi-exclamation-circle"></i> La propiedad debe estar aprobada para gestionar la
      disponibilidad.
    </div>

    <div v-if="errorMessage" class="alert alert-danger py-2 small border-0 mb-3">
      {{ errorMessage }}
    </div>

    <div v-if="showForm" class="card border-0 shadow-sm p-4 mb-4 rounded-4 bg-light">
      <h6 class="fw-bold mb-3">{{ isEditing ? "Editar fechas" : "Nueva disponibilidad" }}</h6>

      <div class="row g-3">
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">INICIO</label>
          <input
            type="date"
            v-model="form.startDate"
            :min="minDate"
            :disabled="isEditing && editingStarted"
            class="form-control border-0 shadow-none"
          />
          <div v-if="errors.startDate" class="text-danger x-small mt-1">{{ errors.startDate }}</div>
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">FIN</label>
          <input
            type="date"
            v-model="form.endDate"
            :min="form.startDate || minDate"
            class="form-control border-0 shadow-none"
          />
          <div v-if="errors.endDate" class="text-danger x-small mt-1">{{ errors.endDate }}</div>
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">PRECIO (€)</label>
          <input
            type="number"
            v-model.number="form.price"
            min="1"
            class="form-control border-0 shadow-none"
            placeholder="0.00"
          />
          <div v-if="errors.price" class="text-danger x-small mt-1">{{ errors.price }}</div>
        </div>
      </div>

      <div v-if="errors.overlap" class="alert alert-danger mt-3 py-2 small border-0">
        {{ errors.overlap }}
      </div>

      <div class="d-flex gap-2 mt-4">
        <button
          @click="handleSubmit"
          class="btn btn-black rounded-pill px-4 btn-sm"
          :disabled="saving"
        >
          {{ saving ? "Guardando..." : "Guardar" }}
        </button>
        <button @click="cancelForm" class="btn btn-link text-dark text-decoration-none btn-sm">
          Cancelar
        </button>
      </div>
    </div>

    <div v-if="availabilities.length > 0" class="d-flex flex-column gap-3">
      <div
        v-for="item in sortedAvailabilities"
        :key="item.id"
        class="card border-0 shadow-sm p-3 rounded-3"
        :class="{ 'opacity-50 bg-light': isPast(item.endDate) }"
      >
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <div class="fw-bold">
              {{ formatDate(item.startDate) }} → {{ formatDate(item.endDate) }}
            </div>
            <div class="x-small text-muted">
              {{ calculateNights(item.startDate, item.endDate) }} noches
            </div>
          </div>
          <div class="text-end d-flex align-items-center gap-3">
            <span class="fw-bold"
              >{{ item.price }}€ <span class="fw-normal text-muted small">/noche</span></span
            >
            <div v-if="!isPast(item.startDate)">
              <button
                @click="editAvailability(item)"
                class="btn btn-sm btn-light rounded-circle me-1"
                title="Editar"
              >
                ✏️
              </button>
              <button
                @click="deleteAvailability(item.id)"
                class="btn btn-sm btn-danger rounded-circle text-danger"
                title="Eliminar"
              >
                🗑️
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!showForm" class="text-center py-4 text-muted border rounded-4 border-dashed">
      <small>No hay fechas configuradas.</small>
    </div>
  </div>
</template>

<script>
import AvailabilityRepository from "@/repositories/AvailabilityRepository";

export default {
  props: {
    propertyId: { type: [String, Number], required: true },
    availabilities: { type: Array, default: () => [] },
    isApproved: { type: Boolean, default: false }
  },
  emits: ["updated"],
  data() {
    return {
      showForm: false,
      isEditing: false,
      editingId: null,
      editingStarted: false,
      saving: false,
      errorMessage: null, // Nuevo campo para errores
      form: { startDate: "", endDate: "", price: null },
      errors: {}
    };
  },
  computed: {
    minDate() {
      return new Date().toISOString().split("T")[0];
    },
    sortedAvailabilities() {
      return [...this.availabilities].sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
    },
    isFormValid() {
      return (
        this.form.startDate &&
        this.form.endDate &&
        this.form.price > 0 &&
        new Date(this.form.endDate) > new Date(this.form.startDate) &&
        !this.errors.overlap &&
        !this.errors.startDate &&
        !this.errors.endDate
      );
    }
  },
  watch: {
    "form.startDate"() {
      this.validateDates();
      this.checkOverlap();
    },
    "form.endDate"() {
      this.validateDates();
      this.checkOverlap();
    },
    "form.price"(val) {
      this.errors.price = val <= 0 || val > 10000 ? "Precio inválido" : "";
    }
  },
  methods: {
    openNewForm() {
      this.errorMessage = null;
      this.resetForm();
      this.showForm = true;
    },
    validateDates() {
      const start = new Date(this.form.startDate);
      const end = new Date(this.form.endDate);
      const today = new Date();

      this.errors.startDate = start < today && !this.editingStarted ? "Fecha pasada" : "";
      this.errors.endDate = end <= start ? "Debe ser posterior al inicio" : "";
    },
    checkOverlap() {
      if (!this.form.startDate || !this.form.endDate) return;
      const start = new Date(this.form.startDate);
      const end = new Date(this.form.endDate);

      const conflict = this.availabilities.find((a) => {
        if (this.isEditing && a.id === this.editingId) return false;
        const aStart = new Date(a.startDate);
        const aEnd = new Date(a.endDate);

        return start <= aEnd && end >= aStart;
      });
      this.errors.overlap = conflict
        ? `Solapa con ${this.formatDate(conflict.startDate)} - ${this.formatDate(conflict.endDate)}`
        : "";
    },
    formatDate(d) {
      return d ? new Date(d).toLocaleDateString("es-ES", { timeZone: "UTC" }) : "";
    },

    calculateNights(s, e) {
      const sd = new Date(s);
      const ed = new Date(e);
      return Math.round((ed - sd) / 86400000) + 1;
    },

    isPast(d) {
      const now = new Date();
      const todayUTC = Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate());
      const dd = new Date(d);
      const dateUTC = Date.UTC(dd.getUTCFullYear(), dd.getUTCMonth(), dd.getUTCDate());
      return dateUTC < todayUTC;
    },
    resetForm() {
      this.form = { startDate: "", endDate: "", price: null };
      this.errors = {};
      this.isEditing = false;
    },
    cancelForm() {
      this.showForm = false;
      this.resetForm();
      this.errorMessage = null;
    },
    editAvailability(a) {
      this.errorMessage = null;
      this.isEditing = true;
      this.editingId = a.id;
      this.editingStarted = this.isPast(a.startDate);
      // Ajuste de fecha para inputs type="date"
      this.form.startDate = new Date(a.startDate).toISOString().split("T")[0];
      this.form.endDate = new Date(a.endDate).toISOString().split("T")[0];
      this.form.price = a.price;
      this.showForm = true;
    },
    async handleSubmit() {
      const start = new Date(this.form.startDate);
      const end = new Date(this.form.endDate);
      const today = new Date();

      this.errors.startDate = !this.form.startDate
        ? "Obligatorio"
        : start < today && !this.editingStarted
          ? "Fecha pasada"
          : "";

      this.errors.endDate = !this.form.endDate
        ? "Obligatorio"
        : end <= start && this.form.startDate && this.form.endDate
          ? "Debe ser posterior al inicio"
          : "";

      this.errors.price =
        this.form.price === null || this.form.price === ""
          ? "Obligatorio"
          : this.form.price <= 0 || this.form.price > 10000
            ? "Precio inválido"
            : "";

      if (this.errors.startDate || this.errors.endDate || this.errors.price) {
        return;
      }
      this.saving = true;
      this.errorMessage = null;
      try {
        const payload = {
          startDate: new Date(this.form.startDate).toISOString(),
          endDate: new Date(this.form.endDate).toISOString(),
          price: this.form.price
        };

        if (this.isEditing) {
          await AvailabilityRepository.update(this.propertyId, this.editingId, payload);
        } else {
          await AvailabilityRepository.create(this.propertyId, payload);
        }

        this.$emit("updated");
        this.cancelForm();
        window.location.reload();
      } catch (e) {
        // Manejo de error mejorado
        this.errorMessage =
          e.response?.data?.message || "Ocurrió un error al guardar la disponibilidad.";
      } finally {
        this.saving = false;
      }
    },
    async deleteAvailability(id) {
      if (!confirm("¿Seguro que quieres eliminar este periodo?")) return;

      this.errorMessage = null;
      try {
        await AvailabilityRepository.delete(this.propertyId, id);
        this.$emit("updated");
        window.location.reload();
      } catch {
        this.errorMessage = "No se pudo eliminar. Puede que tenga reservas asociadas.";
      }
    }
  }
};
</script>

<style scoped>
.btn-black {
  background: #000;
  color: #fff;
}
.btn-black:hover {
  background: #333;
}
.x-small {
  font-size: 0.7rem;
}
.border-dashed {
  border-style: dashed !important;
}
</style>

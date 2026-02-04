<template>
  <div class="container mt-5 mb-5" style="max-width: 800px">
    <h2 class="fw-bold text-center mb-4">
      {{ isEdit ? "Editar Propiedad" : "Publicar Anuncio" }}
    </h2>

    <div v-if="!isEdit" class="alert alert-info text-center small border-0 mb-4">
      <i class="bi bi-info-circle-fill me-2"></i>
      Tu propiedad se creará como <strong>Pendiente de Revisión</strong>.
    </div>

    <div v-else class="alert alert-info text-center small border-0 mb-4">
      <i class="bi bi-info-circle-fill me-2"></i>
      Tu propiedad pasará a estar <strong>Pendiente de Revisión</strong>.
    </div>

    <form @submit.prevent="handleSubmit" class="card border-0 shadow rounded-4 p-4 p-md-5">
      <h5 class="fw-bold border-bottom pb-2 mb-3">Información Básica</h5>

      <div class="mb-3">
        <label class="form-label small fw-bold text-secondary">TÍTULO (Min 5 car.) *</label>
        <input
          v-model="form.title"
          type="text"
          class="form-control bg-light border-0"
          required
          minlength="5"
          maxlength="100"
          placeholder="Ej: Piso luminoso en el centro"
        />
      </div>

      <div class="mb-3">
        <label class="form-label small fw-bold text-secondary">DESCRIPCIÓN (Min 20 car.) *</label>
        <textarea
          v-model="form.description"
          class="form-control bg-light border-0"
          rows="4"
          required
          minlength="20"
          maxlength="255"
          placeholder="Describe tu alojamiento..."
        ></textarea>
      </div>

      <div class="mb-4">
        <label class="form-label small fw-bold text-secondary">TIPO DE PROPIEDAD *</label>
        <select v-model="form.type" class="form-select bg-light border-0" required>
          <option value="APARTMENT">Apartamento</option>
          <option value="CHALET">Chalet</option>
          <option value="TOWNHOUSE">Adosado</option>
          <option value="PENTHOUSE">Ático</option>
          <option value="STUDIO">Estudio</option>
          <option value="DUPLEX">Dúplex</option>
          <option value="COUNTRY_HOUSE">Casa Rural</option>
          <option value="VILLA">Villa</option>
          <option value="BUNGALOW">Bungalow</option>
          <option value="CABIN">Cabaña</option>
          <option value="ESTATE">Finca</option>
          <option value="MANSION">Mansión</option>
          <option value="HOUSE">Casa</option>
          <option value="LOFT">Loft</option>
          <option value="HOTEL_ROOM">Habitación de Hotel</option>
        </select>
      </div>

      <h5 class="fw-bold border-bottom pb-2 mb-3 mt-4">Detalles</h5>
      <div class="row g-3">
        <div class="col-6 col-md-3">
          <label class="form-label small fw-bold text-secondary">HABITACIONES</label>
          <input
            v-model.number="form.bedrooms"
            type="number"
            min="1"
            max="20"
            class="form-control bg-light border-0"
            required
          />
        </div>
        <div class="col-6 col-md-3">
          <label class="form-label small fw-bold text-secondary">BAÑOS</label>
          <input
            v-model.number="form.bathrooms"
            type="number"
            min="1"
            max="20"
            class="form-control bg-light border-0"
            required
          />
        </div>
        <div class="col-6 col-md-3">
          <label class="form-label small fw-bold text-secondary">MAX PERSONAS</label>
          <input
            v-model.number="form.maxOccupancy"
            type="number"
            min="1"
            max="50"
            class="form-control bg-light border-0"
            required
          />
        </div>
        <div class="col-6 col-md-3">
          <label class="form-label small fw-bold text-secondary">M²</label>
          <input
            v-model.number="form.squareMeters"
            type="number"
            min="10"
            max="1000"
            class="form-control bg-light border-0"
            required
          />
        </div>
      </div>

      <div class="d-flex align-items-center justify-content-between mt-4 mb-3 border-bottom">
        <h5 class="fw-bold pb-2 mb-0">Ubicación</h5>
        <small v-if="isEdit" class="text-muted small ms-3">No editable</small>
      </div>
      <div class="row g-3 mb-3">
        <div class="col-md-8">
          <label class="form-label small fw-bold text-secondary">CALLE *</label>
          <input
            v-model="form.street"
            type="text"
            class="form-control bg-light border-0"
            :disabled="isEdit"
            required
          />
        </div>
        <div class="col-md-2">
          <label class="form-label small fw-bold text-secondary">NÚMERO *</label>
          <input
            v-model="form.number"
            type="text"
            class="form-control bg-light border-0"
            :disabled="isEdit"
            required
          />
        </div>
        <div class="col-md-2">
          <label class="form-label small fw-bold text-secondary">PUERTA</label>
          <input
            v-model="form.door"
            type="text"
            class="form-control bg-light border-0"
            :disabled="isEdit"
            placeholder="-"
          />
        </div>
      </div>
      <div class="row g-3">
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">CIUDAD *</label>
          <input
            v-model="form.city"
            type="text"
            class="form-control bg-light border-0"
            :disabled="isEdit"
            required
          />
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">PROVINCIA *</label>
          <input
            v-model="form.province"
            type="text"
            class="form-control bg-light border-0"
            :disabled="isEdit"
            required
          />
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">CP (5 dígitos) *</label>
          <input
            v-model.number="form.postalCode"
            type="number"
            class="form-control bg-light border-0"
            :disabled="isEdit"
            required
            placeholder="15001"
            @keydown="validatePostalCode"
          />
        </div>
      </div>

      <h5 class="fw-bold border-bottom pb-2 mb-3 mt-4">Imagen Principal</h5>
      <div class="mb-4">
        <input
          type="file"
          ref="uploader"
          accept="image/*"
          class="form-control"
          @change="handleImageChange"
        />
        <div v-if="imageSelected || hasImage" class="mt-3 text-center p-3 bg-light rounded">
          <img
            v-if="imageSelected"
            ref="preview"
            class="img-fluid rounded"
            style="max-height: 200px"
            alt="Previsualización nueva"
            src=""
          />
          <img
            v-else
            :src="imageSrc"
            class="img-fluid rounded"
            style="max-height: 200px"
            @error="setPlaceholder"
            alt="Previsualización actual"
          />
        </div>
      </div>

      <div v-if="errorMessage" class="alert alert-danger py-2 small border-0 mb-3">
        {{ errorMessage }}
      </div>

      <div class="d-flex gap-3 mt-4">
        <button
          type="submit"
          class="btn btn-black w-100 py-3 rounded-pill fw-bold"
          :disabled="saving"
        >
          {{ saving ? "Procesando..." : isEdit ? "Guardar Cambios" : "Publicar Propiedad" }}
        </button>
        <router-link
          to="/my-properties"
          class="btn btn-outline-dark w-100 py-3 rounded-pill fw-bold"
          >Cancelar
        </router-link>
      </div>
    </form>
  </div>
</template>

<script>
import PropertyRepository from "@/repositories/PropertyRepository";
import { BACKEND_URL } from "@/constants";

export default {
  props: { id: { type: String, default: null } },
  data() {
    return {
      form: {
        title: "",
        description: "",
        type: "APARTMENT",
        bedrooms: 1,
        bathrooms: 1,
        maxOccupancy: 2,
        squareMeters: 50,
        street: "",
        number: "",
        door: "",
        city: "",
        province: "",
        country: "España",
        postalCode: null
      },
      errorMessage: null,
      saving: false,
      hasImage: false,
      imageSelected: false,
      imageSrc: "/placeholder.png"
    };
  },
  computed: {
    isEdit() {
      return !!this.id;
    }
  },
  async mounted() {
    if (this.isEdit) await this.loadProperty();
  },
  methods: {
    validatePostalCode(e) {
      if (["Backspace", "ArrowLeft", "ArrowRight", "Tab", "Delete"].includes(e.key)) {
        return;
      }
      if (
        this.form.postalCode &&
        this.form.postalCode.toString().length >= 5 &&
        !isNaN(Number(e.key))
      ) {
        e.preventDefault();
      }
    },
    setPlaceholder(e) {
      e.target.src = "https://placehold.co/800x400?text=Sin+Imagen";
    },

    async fetchImage() {
      const token = localStorage.getItem("token"); // o como guardes el token
      const res = await fetch(`${BACKEND_URL}/properties/${this.form.id}/image`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) return;
      const blob = await res.blob();
      this.imageSrc = URL.createObjectURL(blob);
    },
    async loadProperty() {
      try {
        const p = await PropertyRepository.getMyPropertyById(this.id);
        Object.assign(this.form, p);
        this.hasImage = true;
        this.fetchImage();
      } catch {
        this.errorMessage = "Error al cargar la propiedad.";
      }
    },
    handleImageChange(e) {
      const file = e.target.files[0];
      if (file) {
        this.imageSelected = true;
        const reader = new FileReader();
        reader.onload = (ev) => {
          this.$refs.preview.src = String(ev.target.result);
        };
        reader.readAsDataURL(file);
      }
    },
    async handleSubmit() {
      this.saving = true;
      this.errorMessage = null;

      if (String(this.form.postalCode).length !== 5) {
        this.errorMessage = "El código postal debe tener exactamente 5 dígitos.";
        this.saving = false;
        return;
      }

      try {
        let propId = this.id;

        if (this.isEdit) {
          await PropertyRepository.update(this.id, this.form);
        } else {
          const res = await PropertyRepository.create(this.form);
          propId = res.id;
        }

        if (this.$refs.uploader.files[0]) {
          await PropertyRepository.saveImage(propId, this.$refs.uploader.files[0]);
        }

        this.$router.push("/my-properties");
      } catch (e) {
        if (e.response?.data?.message) {
          const serverMsg = e.response.data.message;
          const lower = serverMsg.toLowerCase();
          if (
            serverMsg.includes("Nominatim") ||
            lower.includes("coordinates not found") ||
            lower.includes("coordenadas")
          ) {
            this.errorMessage =
              "Nominatim API no pudo encontrar las coordenadas para la dirección proporcionada.";
          } else {
            this.errorMessage = serverMsg;
          }
        } else {
          this.errorMessage = "Ocurrió un error. Verifica que los datos sean correctos.";
        }
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
.btn-black {
  background-color: #000;
  color: #fff;
  transition: transform 0.2s;
}

.btn-black:hover {
  background-color: #333;
  transform: translateY(-2px);
}

.form-control:focus,
.form-select:focus {
  box-shadow: none;
  border: 1px solid #000 !important;
  background-color: #fff;
}
</style>

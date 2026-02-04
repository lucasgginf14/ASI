<template>
  <div class="filters-sidebar p-4 bg-white border rounded shadow-sm text-center">
    <h5 class="fw-bold mb-3">Filtros</h5>

    <div class="filter-group mb-3 pb-3 border-bottom">
      <label class="fw-bold small mb-2 d-block">Precio por noche</label>
      <div class="d-flex gap-2 justify-content-center">
        <input
          type="number"
          v-model.number="localFilters.priceMin"
          placeholder="Min"
          class="form-control form-control-sm text-center"
          style="max-width: 80px"
          @change="emitFilters"
        />
        <span class="align-self-center">-</span>
        <input
          type="number"
          v-model.number="localFilters.priceMax"
          placeholder="Max"
          class="form-control form-control-sm text-center"
          style="max-width: 80px"
          @change="emitFilters"
        />
      </div>
    </div>

    <div class="filter-group mb-3 pb-3 border-bottom">
      <label class="fw-bold small mb-2 d-block">Tipo de propiedad</label>
      <div
        class="d-flex flex-column gap-2 align-items-start ps-4"
        style="max-height: 200px; overflow-y: auto"
      >
        <label
          v-for="type in propertyTypes"
          :key="type.value"
          class="d-flex align-items-center gap-2 small cursor-pointer text-start"
        >
          <input
            type="checkbox"
            :value="type.value"
            v-model="localFilters.types"
            @change="emitFilters"
          />
          {{ type.label }}
        </label>
      </div>
    </div>

    <div class="filter-group mb-3 pb-3 border-bottom">
      <div class="mb-2">
        <span class="small fw-bold d-block mb-1">Habitaciones</span>
        <div class="d-flex align-items-center justify-content-center gap-3">
          <button
            @click="change('bedrooms', -1)"
            class="btn btn-sm btn-light border rounded-circle"
            :disabled="localFilters.bedrooms <= 0"
          >
            -
          </button>
          <span class="small fw-bold" style="min-width: 20px"
            >{{ localFilters.bedrooms }} o más</span
          >
          <button @click="change('bedrooms', 1)" class="btn btn-sm btn-light border rounded-circle">
            +
          </button>
        </div>
      </div>

      <div>
        <span class="small fw-bold d-block mb-1">Baños</span>
        <div class="d-flex align-items-center justify-content-center gap-3">
          <button
            @click="change('bathrooms', -1)"
            class="btn btn-sm btn-light border rounded-circle"
            :disabled="localFilters.bathrooms <= 0"
          >
            -
          </button>
          <span class="small fw-bold" style="min-width: 20px"
            >{{ localFilters.bathrooms }} o más</span
          >
          <button
            @click="change('bathrooms', 1)"
            class="btn btn-sm btn-light border rounded-circle"
          >
            +
          </button>
        </div>
      </div>
    </div>

    <div class="filter-group mb-4 pb-3 border-bottom">
      <label class="fw-bold small mb-2 d-block">Valoración mínima</label>
      <div class="d-flex gap-1 justify-content-center">
        <span
          v-for="star in 5"
          :key="star"
          class="fs-4"
          :class="star <= localFilters.rating ? 'text-warning' : 'text-muted opacity-25'"
          @click="setRating(star)"
          style="cursor: pointer; transition: transform 0.2s"
          title="Filtrar por estrellas"
        >
          ★
        </span>
      </div>
    </div>

    <button @click="clearFilters" class="btn btn-outline-dark w-100 btn-sm rounded-pill">
      Limpiar todo
    </button>
  </div>
</template>

<script>
export default {
  props: { filters: Object },
  emits: ["update:filters"],
  data() {
    return {
      localFilters: {
        priceMin: null,
        priceMax: null,
        types: [],
        bedrooms: 0,
        bathrooms: 0,
        rating: 0
      },
      // 👇 LISTA COMPLETA DE TU ENUM
      propertyTypes: [
        { value: "APARTMENT", label: "Apartamento" },
        { value: "HOUSE", label: "Casa" },
        { value: "CHALET", label: "Chalet" },
        { value: "VILLA", label: "Villa" },
        { value: "TOWNHOUSE", label: "Adosado" },
        { value: "PENTHOUSE", label: "Ático" },
        { value: "STUDIO", label: "Estudio" },
        { value: "DUPLEX", label: "Dúplex" },
        { value: "LOFT", label: "Loft" },
        { value: "BUNGALOW", label: "Bungalow" },
        { value: "COUNTRY_HOUSE", label: "Casa Rural" },
        { value: "CABIN", label: "Cabaña" },
        { value: "ESTATE", label: "Finca" },
        { value: "MANSION", label: "Mansión" },
        { value: "HOTEL_ROOM", label: "Habitación de Hotel" }
      ]
    };
  },
  mounted() {
    // merge defaults with incoming props so missing keys keep default values
    this.localFilters = { ...this.localFilters, ...(this.filters || {}) };
  },
  watch: {
    filters: {
      handler(newFilters) {
        this.localFilters = { ...this.localFilters, ...(newFilters || {}) };
      },
      deep: true
    }
  },
  methods: {
    emitFilters() {
      // normalize values before emitting
      const payload = {
        priceMin:
          this.localFilters.priceMin === "" || this.localFilters.priceMin == null
            ? null
            : Number(this.localFilters.priceMin),
        priceMax:
          this.localFilters.priceMax === "" || this.localFilters.priceMax == null
            ? null
            : Number(this.localFilters.priceMax),
        types: Array.isArray(this.localFilters.types) ? [...this.localFilters.types] : [],
        bedrooms: Number(this.localFilters.bedrooms) || 0,
        bathrooms: Number(this.localFilters.bathrooms) || 0,
        rating: Number(this.localFilters.rating) || 0
      };
      this.$emit("update:filters", payload);
    },
    change(field, val) {
      const current = Number(this.localFilters[field] || 0);
      const next = current + Number(val);
      if (next >= 0) {
        this.localFilters[field] = next;
        this.emitFilters();
      }
    },
    setRating(star) {
      this.localFilters.rating = this.localFilters.rating === star ? 0 : star;
      this.emitFilters();
    },
    clearFilters() {
      this.localFilters = {
        priceMin: null,
        priceMax: null,
        types: [],
        bedrooms: 0,
        bathrooms: 0,
        rating: 0
      };
      this.emitFilters();
    }
  }
};
</script>

<style scoped>
.fs-4:hover {
  transform: scale(1.2);
}
</style>

<template>
  <div
    class="d-flex align-items-center justify-content-center gap-2 mx-auto search-container"
    style="max-width: 900px"
  >
    <div class="search-pill bg-white rounded-pill shadow-sm border flex-grow-1">
      <form @submit.prevent="onSearch" class="d-flex align-items-center h-100 w-100">
        <div class="search-field px-4 flex-grow-1 position-relative border-end">
          <label class="form-label d-block fw-bold mb-0 text-dark">DESTINO</label>
          <input
            :value="destination"
            @input="$emit('update:destination', $event.target.value)"
            type="text"
            class="form-control border-0 p-0 shadow-none text-truncate"
            placeholder="¿A dónde vas?"
            required
          />
        </div>

        <div class="search-field px-3 border-end">
          <label class="form-label d-block fw-bold mb-0 text-dark">LLEGADA</label>
          <input
            :value="checkIn"
            @input="$emit('update:checkIn', $event.target.value)"
            type="date"
            class="form-control border-0 p-0 shadow-none text-muted small-text"
          />
        </div>

        <div class="search-field px-3 border-end">
          <label class="form-label d-block fw-bold mb-0 text-dark">SALIDA</label>
          <input
            :value="checkOut"
            @input="$emit('update:checkOut', $event.target.value)"
            type="date"
            class="form-control border-0 p-0 shadow-none text-muted small-text"
          />
        </div>

        <div class="search-field px-3" style="width: 140px">
          <label class="form-label d-block fw-bold mb-0 text-dark">HUÉSPEDES</label>
          <input
            :value="guests"
            @input="$emit('update:guests', $event.target.value)"
            type="number"
            min="1"
            class="form-control border-0 p-0 shadow-none text-muted"
            placeholder="¿Cuántos?"
          />
        </div>

        <button type="submit" style="display: none"></button>
      </form>
    </div>

    <button
      @click="onSearch"
      class="btn-logout rounded-pill px-4 fw-bold shadow-sm"
      style="height: 66px"
    >
      <i class="bi bi-search me-2"></i> Buscar
    </button>
  </div>
</template>

<script>
export default {
  name: "SearchBar",
  props: ["destination", "checkIn", "checkOut", "guests"],
  emits: ["update:destination", "update:checkIn", "update:checkOut", "update:guests", "search"],
  methods: {
    onSearch() {
      if (!this.destination?.trim()) {
        alert("Por favor, indica un destino.");
        return;
      }
      this.$emit("search");
    }
  }
};
</script>

<style scoped>
.search-pill {
  height: 66px;
}
.form-label {
  font-size: 0.7rem;
  letter-spacing: 0.5px;
}
.form-control {
  font-size: 0.9rem;
  background: transparent;
}
.search-field {
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  transition: background-color 0.2s;
}
.search-field:first-child {
  border-top-left-radius: 50rem;
  border-bottom-left-radius: 50rem;
}
.search-field:last-of-type {
  border-top-right-radius: 50rem;
  border-bottom-right-radius: 50rem;
  border-right: none !important;
}
.search-field:hover {
  background-color: #f7f7f7;
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
</style>

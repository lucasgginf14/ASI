<template>
  <div class="search-page">
    <section class="pt-3 pb-3 border-bottom bg-white sticky-top" style="z-index: 100">
      <SearchBar
        v-model:destination="search.destination"
        v-model:check-in="search.checkIn"
        v-model:check-out="search.checkOut"
        v-model:guests="search.guests"
        @search="handleSearch"
      />
    </section>

    <div class="container mt-4">
      <div class="row">
        <div class="col-lg-3 mb-4">
          <div class="d-flex bg-white border rounded-3 p-1 mb-4 shadow-sm">
            <button
              class="btn btn-sm flex-grow-1 fw-bold transition"
              :class="viewMode === 'list' ? 'btn-dark' : 'btn-white'"
              @click="viewMode = 'list'"
            >
              <i class="bi bi-list-ul"></i> Lista
            </button>
            <button
              class="btn btn-sm flex-grow-1 fw-bold transition"
              :class="viewMode === 'map' ? 'btn-dark' : 'btn-white'"
              @click="viewMode = 'map'"
            >
              <i class="bi bi-map-fill"></i> Mapa
            </button>
          </div>

          <FiltersSidebar v-model:filters="filters" />
        </div>

        <div class="col-lg-9">
          <div
            class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3"
            v-if="hasSearched && !loading && viewMode === 'list'"
          >
            <h5 class="fw-bold mb-0">
              {{ sortedProperties.length }} alojamientos en "{{ search.destination }}"
            </h5>

            <div class="sort-wrapper">
              <i class="bi bi-arrow-down-up sort-icon"></i>
              <select v-model="sortOption" class="sort-select">
                <option value="" disabled selected>Ordenar por...</option>
                <option value="price_asc">Precio: Menor a mayor</option>
                <option value="price_desc">Precio: Mayor a menor</option>
                <option value="rating_desc">Valoración: Más alta primero</option>
                <option value="">Limpiar</option>
              </select>
              <i class="bi bi-chevron-down chevron-icon"></i>
            </div>
          </div>

          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-dark"></div>
          </div>

          <div v-else-if="viewMode === 'list'">
            <div v-if="sortedProperties.length > 0" class="d-flex flex-column gap-4">
              <PropertyResultCard
                v-for="property in sortedProperties"
                :key="property.id"
                :property="property"
              />
            </div>
            <div v-else class="text-center py-5 bg-light rounded border border-dashed">
              <i class="bi bi-emoji-frown display-4 text-muted mb-3 d-block"></i>
              <h3>No encontramos alojamientos</h3>
              <p class="text-muted">Intenta cambiar los filtros.</p>
            </div>
          </div>

          <div v-else-if="viewMode === 'map'">
            <div v-if="sortedProperties.length > 0">
              <MapResults :properties="sortedProperties" />
            </div>
            <div v-else class="text-center py-5 bg-light rounded border border-dashed">
              <p class="text-muted">No hay propiedades para mostrar en el mapa.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PublicPropertyRepository from "@/repositories/PublicPropertyRepository";
import SearchBar from "./SearchBar.vue";
import FiltersSidebar from "./FiltersSidebar.vue";
import PropertyResultCard from "./PropertyResultCard.vue";
// 👇 IMPORTAMOS EL MAPA
import MapResults from "@/components/search/MapResults.vue";

export default {
  components: { SearchBar, FiltersSidebar, PropertyResultCard, MapResults },
  data() {
    return {
      search: { destination: "", checkIn: "", checkOut: "", guests: null },
      filters: { priceMin: null, priceMax: null, types: [], bedrooms: 0, bathrooms: 0, rating: 0 },
      properties: [],
      loading: false,
      hasSearched: false,
      sortOption: "",

      // 👇 CONTROL DE VISTA ('list' por defecto)
      viewMode: "list"
    };
  },
  computed: {
    filteredProperties() {
      // ... (MANTÉN TU LÓGICA DE FILTRADO ACTUAL EXACTAMENTE IGUAL) ...
      // Para ahorrar espacio, asumo que el código de filtrado de fechas y capacidad sigue aquí
      const toDateOnly = (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate());
      const dayAfter = (d) => {
        const nd = new Date(d);
        nd.setDate(nd.getDate() + 1);
        return toDateOnly(nd);
      };
      const hasDates = this.search.checkIn && this.search.checkOut;
      let searchStart, searchEnd;
      if (hasDates) {
        searchStart = toDateOnly(new Date(this.search.checkIn));
        searchEnd = toDateOnly(new Date(this.search.checkOut));
        if (searchStart > searchEnd) {
          const tmp = searchStart;
          searchStart = searchEnd;
          searchEnd = tmp;
        }
      }

      return this.properties.filter((p) => {
        if (this.filters.types.length > 0 && !this.filters.types.includes(p.type)) return false;
        if (this.filters.bathrooms > 0 && p.bathrooms < this.filters.bathrooms) return false;
        if (this.filters.bedrooms > 0 && p.bedrooms < this.filters.bedrooms) return false;
        if (this.filters.rating > 0 && p.averageRating < this.filters.rating) return false;
        if (this.filters.priceMin != null && p.lowerPrice < this.filters.priceMin) return false;
        if (this.filters.priceMax != null && p.lowerPrice > this.filters.priceMax) return false;

        // ... Logica de huespedes y fechas ...
        const guests = Number(this.search.guests || 0);
        const maxOcc = Number(p.maxOccupancy || 0);
        if (guests > 0 && maxOcc < guests) return false;

        if (hasDates) {
          const avails = (p.availabilities || [])
            .slice()
            .sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
          let remaining = searchStart;
          let covered = false;
          for (const a of avails) {
            const aStart = toDateOnly(new Date(a.startDate));
            const aEnd = toDateOnly(new Date(a.endDate));
            if (aEnd < remaining) continue;
            if (aStart > remaining) {
              covered = false;
              remaining = null;
              break;
            }
            remaining = dayAfter(aEnd);
            if (remaining > searchEnd) {
              covered = true;
              break;
            }
          }
          if (!covered) return false;
        }
        return true;
      });
    },
    sortedProperties() {
      // ... (MANTÉN TU LÓGICA DE ORDENACIÓN ACTUAL) ...
      let list = [...this.filteredProperties];
      if (this.sortOption === "price_desc")
        list.sort((a, b) => (b.lowerPrice || 0) - (a.lowerPrice || 0));
      else if (this.sortOption === "price_asc")
        list.sort((a, b) => (a.lowerPrice || 0) - (b.lowerPrice || 0));
      else if (this.sortOption === "rating_desc")
        list.sort((a, b) => (b.averageRating || 0) - (a.averageRating || 0));
      else if (this.sortOption === "rating_asc")
        list.sort((a, b) => (a.averageRating || 0) - (b.averageRating || 0));
      return list;
    }
  },
  async mounted() {
    const q = this.$route.query;
    if (q.destino) {
      this.search.destination = q.destino;
      this.search.guests = q.huespedes;
      this.search.checkIn = q.llegada;
      this.search.checkOut = q.salida;
      await this.doSearch();
    }
  },
  methods: {
    handleSearch() {
      this.$router.replace({
        query: {
          destino: this.search.destination,
          huespedes: this.search.guests,
          llegada: this.search.checkIn,
          salida: this.search.checkOut
        }
      });
      this.doSearch();
    },
    async doSearch() {
      if (!this.search.destination) return;
      this.loading = true;
      this.hasSearched = true;
      try {
        this.properties = await PublicPropertyRepository.searchByLocation(this.search.destination);
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
/* ESTILOS DE BOTONES LISTA/MAPA */
.transition {
  transition: all 0.2s;
}
.btn-white {
  background: transparent;
  color: #555;
  border: none;
}
.btn-white:hover {
  background: #f0f0f0;
  color: #000;
}
.btn-dark {
  background: #222;
  color: white;
}

/* ... TUS ESTILOS EXISTENTES (sort-wrapper, etc) ... */
.sort-wrapper {
  position: relative;
  display: inline-block;
  min-width: 260px;
}
.sort-select {
  appearance: none;
  -webkit-appearance: none;
  width: 100%;
  padding: 10px 40px;
  font-size: 0.95rem;
  font-weight: 500;
  color: #333;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.02);
}
.sort-select:hover {
  border-color: #b0b0b0;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}
.sort-select:focus {
  outline: none;
  border-color: #222;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.sort-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 1.1rem;
  pointer-events: none;
}
.chevron-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 0.8rem;
  pointer-events: none;
}
</style>

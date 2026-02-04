<template>
  <div class="map-container rounded-3 border shadow-sm">
    <div id="results-map" style="height: 100%; width: 100%"></div>
  </div>
</template>

<script>
import L from "leaflet";
import "leaflet/dist/leaflet.css";

// Fix para iconos de Leaflet en Vue
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow
});

export default {
  props: {
    properties: { type: Array, default: () => [] }
  },
  data() {
    return { map: null, markers: [] };
  },
  mounted() {
    this.initMap();
  },
  watch: {
    properties: {
      handler() {
        this.updateMarkers();
      },
      deep: true
    }
  },
  methods: {
    initMap() {
      // Coordenadas iniciales (Centro de España)
      this.map = L.map("results-map").setView([40.416, -3.703], 6);

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: "&copy; OpenStreetMap contributors"
      }).addTo(this.map);

      this.updateMarkers();
    },
    updateMarkers() {
      if (!this.map) return;

      // 1. Limpiar marcadores antiguos
      this.markers.forEach((m) => this.map.removeLayer(m));
      this.markers = [];

      if (this.properties.length === 0) return;

      const bounds = L.latLngBounds();

      this.properties.forEach((p) => {
        if (p.latitude && p.longitude) {
          // 👇 CAMBIO IMPORTANTE: Creamos un elemento DOM real
          const container = document.createElement("div");
          container.style.textAlign = "center";
          container.style.cursor = "pointer"; // Manita al pasar ratón

          // Contenido HTML del popup
          container.innerHTML = `
            <div class="mb-1"><b>${p.title}</b></div>
            <div class="fw-bold mb-1" style="font-size: 1.1rem;">
              ${p.lowerPrice || p.pricePerNight || p.price || 0}€
            </div>
            <button class="btn btn-sm btn-dark w-100 py-0" style="font-size: 0.8rem;">
              Ver detalles
            </button>
          `;

          // 👇 AÑADIMOS EL EVENTO DE CLICK NATIVO DE VUE
          // Esto evita que la página se recargue completamente
          container.onclick = () => {
            this.$router.push(`/properties/${p.id}`);
          };

          // Creamos el marcador y le asignamos nuestro elemento contenedor
          const marker = L.marker([p.latitude, p.longitude]).bindPopup(container).addTo(this.map);

          this.markers.push(marker);
          bounds.extend([p.latitude, p.longitude]);
        }
      });

      // Ajustar zoom si hay marcadores
      if (this.markers.length > 0) {
        this.map.fitBounds(bounds, { padding: [50, 50] });
      }
    }
  }
};
</script>

<style scoped>
.map-container {
  height: 600px;
  width: 100%;
  z-index: 1;
}
</style>

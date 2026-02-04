<template>
  <div class="card h-100 border-0 shadow-sm rounded-4 p-4">
    <h6 class="fw-bold text-truncate mb-2">{{ stat.title }}</h6>
    <p class="text-muted small mb-3">{{ stat.city }}</p>

    <div class="d-flex justify-content-between align-items-center pt-3 border-top">
      <div class="text-center">
        <span class="d-block fw-bold fs-5">{{ stat.totalBookings }}</span>
        <small class="text-muted">Reservas</small>
      </div>
      <div class="text-center">
        <span class="d-block fw-bold fs-5">{{ stat.totalIncome.toFixed(2) }}€</span>
        <small class="text-muted">Ingresos</small>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    stat: { type: Object, required: true }
  }
};
</script>

<template>
  <button
    type="button"
    class="back-button"
    @click="$router.back()"
    aria-label="Volver"
    style="
      display: inline-flex;
      align-items: center;
      gap: 10px;
      padding: 8px 14px;
      border-radius: 10px;
      border: none;
      background: linear-gradient(180deg, #ffffff, #f0f7ff);
      box-shadow: 0 6px 18px rgba(3, 102, 214, 0.08);
      cursor: pointer;
      color: #0366d6;
      font-weight: 600;
      transition:
        transform 0.12s ease,
        box-shadow 0.12s ease;
    "
  >
    <svg
      width="16"
      height="16"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
      focusable="false"
    >
      <path
        d="M15 18l-6-6 6-6"
        stroke="#0366d6"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
    </svg>
    Volver
  </button>
</template>

import { ref } from "vue";

const store = ref({
  state: {
    user: {
      id: null,
      email: "",
      authority: "",
      logged: false
    }
  }
});

export { getStore };

function getStore() {
  return store.value;
}

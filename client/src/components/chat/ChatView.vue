<template>
  <div class="container mt-4 mb-5" style="max-width: 1100px">
    <div class="d-flex align-items-center gap-3 mb-4">
      <i class="bi bi-chat-dots fs-2"></i>
      <h2 class="fw-bold mb-0">Mensajes</h2>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-dark"></div>
    </div>

    <div v-else class="card border shadow-sm rounded-4 overflow-hidden" style="height: 600px">
      <div class="row g-0 h-100">
        <div class="col-md-4 border-end h-100 d-flex flex-column bg-light">
          <div class="flex-grow-1 overflow-auto">
            <div v-if="conversations.length === 0" class="p-4 text-center text-muted">
              No tienes conversaciones activas.
            </div>
            <div
              v-for="conv in conversations"
              :key="conv.bookingId"
              @click="selectConversation(conv)"
              class="p-3 border-bottom conversation-item cursor-pointer"
              :class="{ 'active-chat': selectedConv?.bookingId === conv.bookingId }"
            >
              <div class="d-flex justify-content-between align-items-start">
                <div class="d-flex gap-3 overflow-hidden">
                  <div class="fs-4 text-secondary"><i class="bi bi-house-door-fill"></i></div>
                  <div>
                    <h6 class="fw-bold mb-1 text-truncate">{{ conv.propertyTitle }}</h6>
                    <small class="text-muted d-block text-truncate">
                      <i class="bi bi-person-fill"></i>
                      {{ conv.user ? conv.user.name : "Usuario" }}
                    </small>
                  </div>
                </div>
                <div
                  v-if="conv.hasUnreadMessages"
                  class="bg-danger rounded-circle"
                  style="width: 10px; height: 10px"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-8 h-100 d-flex flex-column bg-white position-relative">
          <div
            v-if="!selectedConv"
            class="d-flex flex-column align-items-center justify-content-center h-100 text-muted"
          >
            <i class="bi bi-chat-heart display-1 mb-3 opacity-25"></i>
            <h3>Bnbria Chats</h3>
            <p>Selecciona una conversación para empezar.</p>
          </div>

          <template v-else>
            <div
              class="p-3 border-bottom bg-white d-flex align-items-center justify-content-between"
            >
              <div>
                <h5 class="fw-bold mb-0">{{ selectedConv.propertyTitle }}</h5>
                <small class="text-muted">
                  {{ selectedConv.user ? selectedConv.user.name : "Usuario" }}
                </small>
              </div>

              <router-link
                :to="bookingDetailLink"
                class="btn btn-outline-secondary d-flex align-items-center gap-2 px-3 py-2"
                title="Ver reserva"
              >
                <i class="bi bi-card-list"></i>
                <span class="small"><strong>Detalles de la reserva ➡️</strong></span>
              </router-link>
            </div>

            <div
              class="flex-grow-1 p-4 overflow-auto d-flex flex-column gap-3"
              ref="messagesContainer"
            >
              <div v-if="loadingMessages" class="text-center mt-5">
                <div class="spinner-border spinner-border-sm text-secondary"></div>
              </div>

              <div v-else-if="messages.length === 0" class="text-center mt-5 text-muted small">
                <i class="bi bi-chat-square-text fs-3 d-block mb-2"></i>
                Escribe el primer mensaje para iniciar la conversación.
              </div>

              <div v-else>
                <template v-for="(msg, index) in messages" :key="index">
                  <div
                    v-if="
                      index === 0 || formatDay(msg.sentAt) !== formatDay(messages[index - 1].sentAt)
                    "
                    class="d-flex justify-content-center my-2"
                  >
                    <div class="px-3 py-1 rounded-pill bg-secondary text-white small">
                      {{ formatDay(msg.sentAt) }}
                    </div>
                  </div>

                  <div
                    class="d-flex align-items-end mb-2"
                    :class="msg.senderMe ? 'justify-content-end' : 'justify-content-start'"
                  >
                    <div
                      class="p-3 shadow-sm"
                      :style="
                        msg.senderMe
                          ? {
                              background: '#a5d9f3',
                              color: '#114b75',
                              maxWidth: '75%',
                              borderRadius: '12px'
                            }
                          : {
                              background: '#f8f9fa',
                              color: '#212529',
                              maxWidth: '75%',
                              borderRadius: '12px'
                            }
                      "
                    >
                      <div style="white-space: pre-line">{{ msg.text }}</div>
                      <div class="text-end mt-1" style="font-size: 0.65rem; opacity: 0.75">
                        {{ formatTime(msg.sentAt) }}
                      </div>
                    </div>
                  </div>
                </template>
              </div>
            </div>

            <div class="p-3 border-top bg-light">
              <div class="input-group">
                <input
                  v-model="newMessage"
                  @keyup.enter="sendMessage"
                  type="text"
                  class="form-control border-0 shadow-sm"
                  placeholder="Escribe un mensaje..."
                  :disabled="sending"
                />
                <button
                  @click="sendMessage"
                  class="btn btn-dark px-4 d-flex align-items-center justify-content-center"
                  :disabled="!newMessage.trim() || sending"
                >
                  <svg
                    v-if="!sending"
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    fill="currentColor"
                    class="bi bi-send-fill text-white"
                    viewBox="0 0 16 16"
                  >
                    <path
                      d="M15.964.686a.5.5 0 0 0-.65-.65L.767 5.855H.766l-.452.18a.5.5 0 0 0-.082.887l.41.26.001.002 4.995 3.178 3.178 4.995.002.002.26.41a.5.5 0 0 0 .886-.083l6-15Zm-1.833 1.89L6.637 10.07l-.215-.338a.5.5 0 0 0-.154-.154l-.338-.215 7.494-7.494 1.178-.471-.47 1.178Z"
                    />
                  </svg>
                  <i v-if="!sending" class="bi bi-send-fill text-white"></i>
                  <span v-else class="spinner-border spinner-border-sm text-white"></span>
                </button>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MessageRepository from "@/repositories/MessageRepository";
// 👇 1. IMPORTAMOS EL REPOSITORIO DE RESERVAS
import BookingRepository from "@/repositories/BookingRepository";
import { getStore } from "@/common/store";

export default {
  data() {
    return {
      conversations: [],
      selectedConv: null,
      messages: [],
      newMessage: "",
      loading: true,
      loadingMessages: false,
      sending: false,
      currentUserId: null,
      currentUserRole: "GUEST" // Valor por defecto
    };
  },
  computed: {
    bookingDetailLink() {
      if (!this.selectedConv) return "#";
      const id = this.selectedConv.bookingId;
      return this.currentUserRole === "HOST" ? `/my-reservations-host/${id}` : `/my-bookings/${id}`;
    }
  },
  async mounted() {
    const store = getStore();
    this.currentUserId = store.state.user?.id || null;

    await this.loadConversations();
    this.checkUrlParam();
  },
  methods: {
    async loadConversations() {
      try {
        const convs = await MessageRepository.getConversations();
        this.conversations = (convs || []).sort(
          (a, b) => new Date(b.lastMessage || 0) - new Date(a.lastMessage || 0)
        );
      } catch (e) {
        console.error(e);
      } finally {
        this.loading = false;
      }
    },

    checkUrlParam() {
      const { bookingId, title, name, role } = this.$route.query;

      if (bookingId) {
        const idToSearch = Number(bookingId);

        // Si la URL dice explícitamente HOST, confiamos en ello inicialmente
        if (role === "HOST") {
          this.currentUserRole = "HOST";
        }

        let targetConv = this.conversations.find((c) => c.bookingId === idToSearch);

        if (!targetConv) {
          targetConv = {
            bookingId: idToSearch,
            propertyTitle: title || "Reserva #" + bookingId,
            user: { name: name || "Usuario" },
            hasUnreadMessages: false,
            isNew: true
          };
          this.conversations.unshift(targetConv);
        }

        this.selectConversation(targetConv);
      }
    },

    // 👇 2. MODIFICAMOS LA SELECCIÓN PARA DETECTAR EL ROL
    async selectConversation(conv) {
      this.selectedConv = conv;
      this.messages = [];
      conv.hasUnreadMessages = false;

      // Si es una conversación nueva (falsa), no podemos chequear en backend aún.
      // Mantenemos el rol que fijó checkUrlParam.
      if (conv.isNew) {
        this.loadingMessages = false;
        return;
      }

      this.loadingMessages = true;

      try {
        // Lanzamos en paralelo la carga de mensajes y la comprobación de rol
        const [msgs, isHost] = await Promise.all([
          MessageRepository.getMessages(conv.bookingId),
          this.checkIfHost(conv.bookingId)
        ]);

        this.messages = msgs.map((m) => ({
          ...m,
          senderMe: m.writer?.id === this.currentUserId
        }));

        // Actualizamos el rol según lo que diga el backend
        this.currentUserRole = isHost ? "HOST" : "GUEST";

        this.scrollToBottom();
      } catch (e) {
        console.error(e);
      } finally {
        this.loadingMessages = false;
      }
    },

    async checkIfHost(id) {
      try {
        // Intentamos pedir el detalle como HOST. Si funciona, somos el dueño.
        await BookingRepository.getHostBookingDetails(id);
        return true;
      } catch {
        // Si da error (403 Forbidden o 404), es que NO somos el dueño (somos Guest)
        return false;
      }
    },

    async sendMessage() {
      if (!this.newMessage.trim()) return;
      this.sending = true;

      try {
        const sentMsg = await MessageRepository.sendMessage(
          this.selectedConv.bookingId,
          this.newMessage
        );

        this.messages.push({ ...sentMsg, senderMe: true });
        this.newMessage = "";

        if (this.selectedConv.isNew) {
          this.selectedConv.isNew = false;
        }

        this.scrollToBottom();
      } catch {
        alert("Error al enviar mensaje");
      } finally {
        this.sending = false;
      }
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const c = this.$refs.messagesContainer;
        if (c) c.scrollTop = c.scrollHeight;
      });
    },
    formatTime(dateStr) {
      if (!dateStr) return "Justo ahora";
      return new Date(dateStr).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    },
    formatDay(dateStr) {
      if (!dateStr) return "";
      const d = new Date(dateStr);
      const today = new Date();
      if (d.toDateString() === today.toDateString()) return "Hoy";
      return d.toLocaleDateString();
    }
  }
};
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.conversation-item:hover {
  background-color: #f8f9fa;
}
.active-chat {
  background-color: #e9ecef;
  border-left: 4px solid #000;
}
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 4px;
}
</style>

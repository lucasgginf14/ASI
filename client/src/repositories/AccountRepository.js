import HTTP from "../common/http";

export default {
  async authenticate(credentials) {
    return (await HTTP.post(`authenticate`, credentials)).data;
  },

  async getAccount() {
    return (await HTTP.get(`account`)).data;
  },

  async registerAccount(user) {
    return (await HTTP.post(`register`, user)).data;
  },

  async getAccountInfo() {
    return (await HTTP.get(`account/info`)).data;
  },

  async getAppStats() {
    return (await HTTP.get(`app-stats`)).data;
  }
};

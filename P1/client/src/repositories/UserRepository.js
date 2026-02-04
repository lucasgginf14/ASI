import HTTP from "@/common/http";

const resource = "users";

export default {
  async findAll() {
    return (await HTTP.get(`${resource}`)).data;
  },
  async delete(userId) {
    return (await HTTP.delete(`${resource}/${userId}`)).data;
  },
  async activate(userId) {
    return (await HTTP.put(`${resource}/${userId}/active`)).data;
  },
  async deactivate(userId) {
    return (await HTTP.delete(`${resource}/${userId}/active`)).data;
  }
};

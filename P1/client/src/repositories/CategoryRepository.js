import HTTP from "@/common/http";

const resource = "categories";

export default {
  async findAll() {
    return (await HTTP.get(resource)).data;
  },
  async findOne(categoryId) {
    const response = await HTTP.get(`${resource}/${categoryId}`);
    return response.data;
  },
  async create(category) {
    const response = await HTTP.post(`${resource}`, category);
    return response.data;
  },
  async update(categoryId, category) {
    const response = await HTTP.put(`${resource}/${categoryId}`, category);
    return response.data;
  },
  async delete(categoryId) {
    const response = await HTTP.delete(`${resource}/${categoryId}`);
    return response.data;
  }
};

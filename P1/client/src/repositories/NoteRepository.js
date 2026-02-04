import HTTP from "@/common/http";

const resource = "notes";

function applyDate(note) {
  note.timestamp = new Date(note.timestamp);
  return note;
}

export default {
  async findAll() {
    const response = await HTTP.get(`${resource}`);
    response.data.forEach(applyDate);
    return response.data;
  },
  async findOne(noteId) {
    const response = await HTTP.get(`${resource}/${noteId}`);
    response.data.timestamp = new Date(response.data.timestamp);
    return response.data;
  },
  async create(note) {
    const response = await HTTP.post(`${resource}`, note);
    response.data.timestamp = new Date(response.data.timestamp);
    return response.data;
  },
  async update(note) {
    const response = await HTTP.put(`${resource}/${note.id}`, note);
    response.data.timestamp = new Date(response.data.timestamp);
    return response.data;
  },
  async delete(noteId) {
    await HTTP.delete(`${resource}/${noteId}`);
  },
  async changeOwner(noteId, userId) {
    const response = await HTTP.put(`${resource}/${noteId}/owner`, { userId });
    return response.data;
  }
};

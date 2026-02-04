import { getStore } from "./store";
import AccountRepository from "../repositories/AccountRepository";

export default {
  login,
  logout,
  getToken,
  isAdmin,
  isAuthenticated,
  isAuthenticationChecked: isAuthenticationChecked(),
  register,
  getAccountInfo,
  getAppStats
};

async function login(credentials) {
  const response = await AccountRepository.authenticate(credentials);
  _saveToken(response.token);
  return _authenticate();
}

function logout() {
  _removeToken();
  const store = getStore();
  store.state.user.id = null;
  store.state.user.email = "";
  store.state.user.authority = "";
  store.state.user.logged = false;
}

function isAdmin() {
  return getStore().state.user.authority === "ADMIN";
}

function isAuthenticated() {
  return getStore().state.user.logged;
}

function getToken() {
  return localStorage.getItem("token");
}

function _saveToken(token) {
  localStorage.setItem("token", token);
}

function _removeToken() {
  localStorage.removeItem("token");
}

async function _authenticate() {
  const response = await AccountRepository.getAccount();
  const store = getStore();
  store.state.user.id = response.id;
  store.state.user.email = response.email;
  store.state.user.authority = response.authority;
  store.state.user.logged = true;
  return store.state.user;
}

function isAuthenticationChecked() {
  return new Promise((resolve) => {
    if (getToken()) {
      _authenticate()
        .catch(() => logout())
        .finally(() => resolve(true));
    } else {
      resolve(true);
    }
  });
}

async function register(user) {
  await AccountRepository.registerAccount(user);
}

async function getAccountInfo() {
  return await AccountRepository.getAccountInfo();
}

async function getAppStats() {
  return await AccountRepository.getAppStats();
}

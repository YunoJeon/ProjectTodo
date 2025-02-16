import axios from "axios";

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  withCredentials: true,
});

const publicEndpoints = [
    "/auth/sign-up",
    "/auth/sign-in",
    "/auth/sign-out",
    "/auth/check-email",
    "/auth/refresh"
]

api.interceptors.request.use(
    (config) => {

      // @ts-ignore
      if (!config.url || publicEndpoints.some(endpoint => config.url.startsWith(endpoint))) {
        return config;
      }

      const token = localStorage.getItem('accessToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;
      if (error.response && error.response.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const refreshResponse = await api.post("/auth/refresh");
          const newAccessToken = refreshResponse.data.accessToken;
          localStorage.setItem('accessToken', newAccessToken);

          return api(originalRequest);
        } catch (refreshError) {
          return Promise.reject(refreshError);
        }
      }
      return Promise.reject(error);
    }
);

export default api;
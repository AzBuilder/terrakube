import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: window._env_.REACT_APP_TERRAKUBE_API_URL
});

export const axiosClient = axios.create({
  baseURL: window._env_.REACT_APP_TERRAKUBE_API_URL
});

axiosInstance.interceptors.request.use(
  function(config) {
    if(localStorage.getItem('azureAccessToken')) {
      config.headers["Authorization"] = `Bearer ${localStorage.getItem('azureAccessToken')}`;
    }
    return config;
  },
  function(error) {
    Promise.reject(error);
  }
)


export default axiosInstance;

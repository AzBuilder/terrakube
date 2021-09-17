import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: process.env.REACT_APP_BUILDER_API_URL
});

export const axiosClient = axios.create({
  baseURL: process.env.REACT_APP_BUILDER_API_URL
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

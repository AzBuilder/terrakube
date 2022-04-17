import { CalculatorOutlined } from '@ant-design/icons';
import axios from 'axios';
import {msalInstance,loginRequest } from "./authConfig";

const axiosInstance = axios.create({
    baseURL: window._env_.REACT_APP_TERRAKUBE_API_URL
});

export const axiosClient = axios.create({
  baseURL: window._env_.REACT_APP_TERRAKUBE_API_URL
});

axiosInstance.interceptors.request.use(
  function(config) {
    const activeAccount = msalInstance.getActiveAccount();
    const accounts = msalInstance.getAllAccounts();
    msalInstance.acquireTokenSilent({...loginRequest,account: activeAccount || accounts[0]}).then((accessTokenResponse) => {
      let accessToken = accessTokenResponse.accessToken;
      localStorage.setItem('azureAccessToken', accessToken);
    });


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

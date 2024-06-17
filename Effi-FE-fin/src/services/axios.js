import axios from 'axios';
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth.js';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000,
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });

    failedQueue = [];
};

axiosInstance.interceptors.request.use(
    config => {
        const accessToken = sessionStorage.getItem('accessToken');
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        } else {
            // Access token이 없는 경우 Authorization 헤더 제거
            delete config.headers.Authorization;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    response => response,
    error => {
      const { config, response: { status, data } } = error;
      const originalRequest = config;
  
      if (status == 401 && !originalRequest._retry) {
        if (data.msg == "토큰이 만료되었습니다. 다시 로그인해주세요.") {
          if (isRefreshing) {
            return new Promise(function(resolve, reject) {
              failedQueue.push({resolve, reject});
            })
            .then(token => {
              originalRequest.headers['Authorization'] = 'Bearer ' + token;
              return axiosInstance(originalRequest);
            })
            .catch(err => {
              return Promise.reject(err);
            });
          }
  
          originalRequest._retry = true;
          isRefreshing = true;
  
          const refreshToken = sessionStorage.getItem('refreshToken');
          return new Promise(function(resolve, reject) {
            axiosInstance.post('/api/auth/test', {}, {
                headers: {
                  'Refresh-Token': 'Bearer ' + refreshToken
                }
              })
              .then(response => {
                const accessToken = response.headers['new-access-token'];
                sessionStorage.setItem('accessToken', accessToken);
                useAuthStore.accessToken = accessToken;
                originalRequest.headers['Authorization'] = 'Bearer ' + accessToken;

                processQueue(null, accessToken);
                resolve(axiosInstance(originalRequest));
              })
              .catch((error) => {
                processQueue(error, null);
                reject(error);
              })
              .finally(() => {
                isRefreshing = false;
              });
          });
        }
      }
      return Promise.reject(error);
    }
  );

export default axiosInstance;
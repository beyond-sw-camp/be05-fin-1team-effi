import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8080',
});

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

export default axiosInstance;
import axios from 'axios';

// 建立一個 axios 實體，設定好後端的基礎網址
const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// 請求攔截器：每次發送請求到後端「之前」，都會經過這裡
api.interceptors.request.use(
  (config) => {
    // 嘗試從瀏覽器的 LocalStorage 中拿出 Token
    const token = localStorage.getItem('token');
    
    // 如果有找到 Token，就自動把它塞進 Authorization Header 裡
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;
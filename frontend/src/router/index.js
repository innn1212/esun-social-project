import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 1. 真實的首頁客廳
    {
      path: '/home',
      name: 'home',
      component: HomeView // 對應的畫面
    },
    // 2. 當使用者只輸入 localhost:5173/ 時，自動轉交給 /home
    {
      path: '/',
      redirect: '/home'
    },
    // 3. 登入頁面
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    // 4. 捕捉所有打錯的網址 (例如 /abc, /homm)，一律丟回 /home
    {
      path: '/:catchAll(.*)',
      redirect: '/home'
    }
  ]
})

// 路由守衛 (警衛)：每次切換網址前都會執行
router.beforeEach((to, from, next) => {
  // 檢查 LocalStorage 裡面有沒有 Token
  const isAuthenticated = localStorage.getItem('token');
  
  // 如果他想去的頁面「不是」登入頁，且他「沒有」Token
  if (to.name !== 'login' && !isAuthenticated) {
    next({ name: 'login' }) // 強制導向登入頁
  } else {
    next() // 沒問題，放行！
  }
})

export default router
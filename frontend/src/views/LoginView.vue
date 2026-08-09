<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../utils/api'; // 引入我們剛剛寫好的 axios 武器

const router = useRouter();

// 定義響應式變數 (這些變數會跟畫面上的輸入框綁定在一起)
const isLoginMode = ref(true); // 控制現在是登入(true)還是註冊(false)模式
const phoneNumber = ref('');
const password = ref('');
const userName = ref('');
const email = ref('');

// 送出表單的處理邏輯
const handleSubmit = async () => {
  try {
    if (isLoginMode.value) {
      // 【執行登入 API】
      const response = await api.post('/users/login', {
        phoneNumber: phoneNumber.value,
        password: password.value
      });
      
      // 登入成功：把 Token 存入瀏覽器的 LocalStorage
      localStorage.setItem('token', response.data.token);
      alert('登入成功！');
      
      // 透過 router 導向動態牆首頁 ('/')
      router.push('/');
      
    } else {
      // 【執行註冊 API】
      await api.post('/users/register', {
        phoneNumber: phoneNumber.value,
        password: password.value,
        userName: userName.value,
        email: email.value
      });
      
      alert('註冊成功！請使用新帳號登入。');
      // 註冊成功後，清空密碼並自動切換回登入模式
      password.value = '';
      isLoginMode.value = true;
    }
  } catch (error) {
    // 捕捉後端回傳的錯誤訊息 (例如帳號錯誤、密碼錯誤)
    const errorMsg = error.response?.data?.error || error.response?.data || '發生未知錯誤';
    alert(errorMsg);
  }
};
</script>

<template>
  <div class="auth-container">
    <div class="auth-box">
      <h2>{{ isLoginMode ? '登入玉山社群' : '註冊新帳號' }}</h2>
      
      <!-- @submit.prevent 代表阻止表單預設的重整網頁行為，改交由 handleSubmit 處理 -->
      <form @submit.prevent="handleSubmit">
        
        <!-- 手機號碼 (登入註冊都需要) -->
        <!-- v-model="phoneNumber" 會將這個輸入框跟我們上面的變數綁定 -->
        <div class="form-group">
          <label>手機號碼</label>
          <input type="text" v-model="phoneNumber" required placeholder="請輸入手機號碼">
        </div>

        <!-- 姓名與 Email (使用 v-if 判斷，只有註冊模式才顯示這兩塊) -->
        <template v-if="!isLoginMode">
          <div class="form-group">
            <label>使用者名稱</label>
            <input type="text" v-model="userName" required placeholder="請輸入姓名">
          </div>
          <div class="form-group">
            <label>電子郵件</label>
            <input type="email" v-model="email" required placeholder="請輸入 Email">
          </div>
        </template>

        <!-- 密碼 (登入註冊都需要) -->
        <div class="form-group">
          <label>密碼</label>
          <input type="password" v-model="password" required placeholder="請輸入密碼">
        </div>

        <button type="submit" class="submit-btn">
          {{ isLoginMode ? '登入' : '註冊' }}
        </button>
      </form>

      <!-- 切換模式的文字按鈕 -->
      <p class="toggle-text">
        {{ isLoginMode ? '還沒有帳號嗎？' : '已經有帳號了？' }}
        <!-- 點擊時，將 isLoginMode 的 true/false 反轉 -->
        <span @click="isLoginMode = !isLoginMode" class="toggle-link">
          {{ isLoginMode ? '點此註冊' : '點此登入' }}
        </span>
      </p>
    </div>
  </div>
</template>

<style scoped>
/* 簡單的美化樣式 (玉山綠主題) */
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.auth-box {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
}
.form-group {
  margin-bottom: 15px;
}
.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
}
.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}
.submit-btn {
  width: 100%;
  padding: 10px;
  background-color: #009e96; 
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 10px;
}
.submit-btn:hover {
  background-color: #007a74;
}
.toggle-text {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}
.toggle-link {
  color: #009e96;
  cursor: pointer;
  font-weight: bold;
}
</style>
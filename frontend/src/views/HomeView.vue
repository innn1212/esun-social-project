<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../utils/api';

const router = useRouter();

const posts = ref([]); 
const newPostContent = ref(''); 
const newPostImage = ref('');   

// --- 編輯功能專用的變數 ---
const editingPostId = ref(null); // 紀錄目前正在編輯哪一篇文章的 ID
const editContent = ref('');     // 綁定編輯框的文字
const editImage = ref('');       // 綁定編輯框的圖片網址

const fetchPosts = async () => {
  try {
    const response = await api.get('/posts');
    posts.value = response.data;
  } catch (error) {
    console.error('取得文章失敗：', error);
  }
};

onMounted(() => {
  fetchPosts();
});

const handleCreatePost = async () => {
  if (!newPostContent.value) return alert('請輸入發文內容！');
  try {
    await api.post('/posts', {
      content: newPostContent.value,
      image: newPostImage.value || null
    });
    alert('發文成功！');
    newPostContent.value = '';
    newPostImage.value = '';
    fetchPosts();
  } catch (error) {
    alert(error.response?.data || '發文失敗');
  }
};

const handleLogout = () => {
  localStorage.removeItem('token');
  alert('已登出系統！');
  router.push('/login');
};

// --- 以下為新增的編輯與刪除邏輯 ---

// 點擊「編輯」按鈕時觸發
const startEdit = (post) => {
  editingPostId.value = post.postId; // 讓畫面知道現在這篇進入編輯模式
  editContent.value = post.content;  // 把舊內容塞進編輯框
  editImage.value = post.image || ''; // 把舊圖片塞進編輯框 (如果是 null 轉成空字串)
};

// 點擊「取消」按鈕時觸發
const cancelEdit = () => {
  editingPostId.value = null; // 清空狀態，恢復成瀏覽模式
};

// 送出編輯後的資料 (呼叫我們寫好的 PATCH API)
const handleUpdatePost = async (postId) => {
  try {
    await api.patch(`/posts/${postId}`, {
      content: editContent.value,
      image: editImage.value // 如果使用者清空了輸入框，這裡就會傳送空字串 "" 給後端刪除圖片
    });
    alert('文章更新成功！');
    editingPostId.value = null; // 關閉編輯模式
    fetchPosts(); // 重新撈取最新文章
  } catch (error) {
    alert(error.response?.data || '更新失敗');
  }
};

// 點擊「刪除」按鈕時觸發
const handleDeletePost = async (postId) => {
  // 瀏覽器內建的防呆確認視窗
  if (!confirm('確定要刪除這篇文章嗎？底下的留言也會跟著消失喔！')) {
    return; 
  }

  try {
    await api.delete(`/posts/${postId}`);
    alert('文章刪除成功！');
    fetchPosts(); // 重新撈取最新文章
  } catch (error) {
    // 如果你嘗試刪除別人的文章，後端就會噴錯誤到這裡！
    alert(error.response?.data || '刪除失敗');
  }
};

// --- 留言功能專用的變數 ---
// 用來存放每一篇文章的留言輸入框內容，格式會像這樣：{ 1: '第一篇的留言', 2: '第二篇的留言' }
const newComments = ref({});

// 新增留言的處理邏輯
const handleAddComment = async (postId) => {
  const content = newComments.value[postId];
  
  if (!content) {
    alert('請輸入留言內容！');
    return;
  }

  try {
    // 呼叫我們後端寫好的新增留言 API
    await api.post(`/posts/${postId}/comments`, { content });
    
    // 留言成功後，清空該篇文章的輸入框
    newComments.value[postId] = '';

    // 補上這行！重新撈取最新文章與留言，讓畫面瞬間更新！
    fetchPosts(); 

    // 把 alert 移到最後面，這樣畫面更新後才會跳出提示，體驗更好
    alert('留言成功！');
    
  } catch (error) {
    alert(error.response?.data || '留言失敗');
  }
};
</script>

<template>
  <div class="home-container">
    <header class="header">
      <h1>玉山匿名社群動態牆</h1>
      <button @click="handleLogout" class="logout-btn">登出</button>
    </header>

    <section class="create-post-card">
      <textarea v-model="newPostContent" placeholder="你在想什麼呢？" rows="3"></textarea>
      <input type="text" v-model="newPostImage" placeholder="圖片網址 (選填)" />
      <div class="action-bar">
        <button @click="handleCreatePost" class="post-btn">發布</button>
      </div>
    </section>

    <section class="feed-section">
      <div v-for="post in posts" :key="post.postId" class="post-card">
        
        <div class="post-header">
          <span class="author"> {{ post.userId }} 號社畜</span>
          
          <!-- 只有不在編輯模式時，才顯示編輯與刪除按鈕 -->
          <div class="post-actions" v-if="editingPostId !== post.postId">
            <button @click="startEdit(post)" class="action-btn edit">編輯</button>
            <button @click="handleDeletePost(post.postId)" class="action-btn delete">刪除</button>
          </div>
        </div>
        
        <!-- 如果現在這篇文章是被點擊編輯的那一篇，顯示編輯框 (v-if) -->
        <div v-if="editingPostId === post.postId" class="edit-mode">
          <textarea v-model="editContent" rows="3"></textarea>
          <input type="text" v-model="editImage" placeholder="圖片網址 (輸入空字串可刪除圖片)" />
          <div class="edit-action-bar">
            <button @click="handleUpdatePost(post.postId)" class="post-btn">儲存修改</button>
            <button @click="cancelEdit" class="cancel-btn">取消</button>
          </div>
        </div>
        
        <!-- 否則，正常顯示純文字與圖片 (v-else) -->
        <div v-else class="post-body">
          <p>{{ post.content }}</p>
          <img v-if="post.image" :src="post.image" alt="發文圖片" class="post-image" />
          <div class="time">{{ new Date(post.createdAt).toLocaleString() }}</div>

          <!-- 新增的留言列表區塊 -->
          <div class="comments-list" v-if="post.comments && post.comments.length > 0">
            <!-- 針對這篇文章的每一筆留言跑迴圈 -->
            <div v-for="comment in post.comments" :key="comment.commentId" class="comment-item">
              <span class="comment-author">{{ comment.userId }} 號社畜:</span>
              <span class="comment-text">{{ comment.content }}</span>
            </div>
          </div>
          <!-- 新增的留言列表區塊 -->

          <!-- 新增的留言輸入區塊 -->
          <div class="comment-section">
            <input 
            type="text" 
            v-model="newComments[post.postId]" 
            placeholder="留個言吧..." 
            class="comment-input"
            @keyup.enter="handleAddComment(post.postId)"
            />
            <button @click="handleAddComment(post.postId)" class="action-btn comment-btn">送出</button>
          </div>
          <!-- 新增的留言輸入區塊 -->
        </div>
        
      </div>
      
      <div v-if="posts.length === 0" class="no-post">目前還沒有任何貼文，趕快搶頭香吧！</div>
    </section>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.logout-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}
.create-post-card, .post-card {
  background: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}
textarea, input[type="text"] {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
  font-family: inherit;
}
.action-bar {
  text-align: right;
}
.post-btn {
  background-color: #009e96;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}
.post-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}
/* --- 優化 1: 讓發文者名稱變成搶眼的玉山綠「藥丸徽章」設計 --- */
.author {
  font-weight: 900;             /* 字體加粗 */
  color: #333;               /* 玉山綠字體 */
  font-size: 15px;
  background-color: #dbdbdb;    /* 淡淡的綠色背景 */
  padding: 4px 12px;            /* 內邊距撐開背景 */
  border-radius: 20px;          /* 圓角變成圓弧徽章 */
  display: inline-block;        /* 讓排版更獨立 */
}
.post-body p {
  margin-top: 0;
  white-space: pre-wrap; /* 讓換行符號能正確顯示 */
}

/* --- 優化 2: 縮小圖片尺寸，並保持比例 --- */
.post-image {
  max-width: 100%;
  max-height: 250px;            /* 限制最大高度，避免圖片太長佔滿整個螢幕 */
  object-fit: contain;          /* 保持圖片原始比例，不會被壓扁變形 */
  border-radius: 8px;           /* 圓角 */
  margin-top: 10px;
  background-color: #f8f9fa;    /* 如果圖片有透明背景，給個淡灰色襯底 */
  border: 1px solid #eee;       /* 加個極細的邊框讓圖片更立體 */
}
.no-post {
  text-align: center;
  color: #999;
  padding: 20px;
}
.home-container { max-width: 600px; margin: 0 auto; padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.logout-btn { background-color: #dc3545; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
.create-post-card, .post-card { background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin-bottom: 20px; }
textarea, input[type="text"] { width: 100%; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; padding: 10px; margin-bottom: 10px; font-family: inherit; }
.action-bar, .edit-action-bar { text-align: right; }
.post-btn { background-color: #009e96; color: white; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.cancel-btn { background-color: #6c757d; color: white; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; margin-left: 10px; }
.post-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }

.time { font-size: 12px; color: #999; margin-top: 10px; }
.post-body p { margin-top: 0; white-space: pre-wrap; }
.post-image { max-width: 100%; border-radius: 4px; margin-top: 10px; }
.no-post { text-align: center; color: #999; padding: 20px; }
.action-btn { border: none; padding: 4px 8px; border-radius: 4px; cursor: pointer; font-size: 12px; margin-left: 5px; }
.edit { background-color: #ffc107; color: #333; }
.delete { background-color: #dc3545; color: white; }

/* --- 優化 3: 留言區塊與縮短按鈕 --- */
.comment-section {
  display: flex;
  align-items: center;          /* 讓輸入框和按鈕垂直置中對齊 */
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}
.comment-input {
flex-grow: 1;                 /* 讓輸入框盡可能佔滿剩下的空間 */
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  margin-bottom: 0; /* 覆寫原本 input 的 margin */
}
.comment-btn {
  background-color: #007bff;
  color: white;
  margin-left: 10px;
  padding: 8px 16px;            /* 縮小寬度，讓按鈕看起來精緻短小 */
  flex-shrink: 0;               /* 確保按鈕不會被輸入框擠壓變形 */
  border-radius: 4px;
}
.comment-btn:hover {
  background-color: #0056b3;
}

.comments-list {
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px dashed #ccc;
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
}
.comment-item {
  margin-bottom: 8px;
  font-size: 14px;
}
.comment-item:last-child {
  margin-bottom: 0;
}
.comment-author {
  font-weight: bold;
  color: #009e96;
  margin-right: 8px;
}
.comment-text {
  color: #333;
}
</style>
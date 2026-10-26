<template>
  <header class="nav-bar">
    <div class="nav-inner">
      <router-link to="/" class="logo">林间博客</router-link>
      <nav class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/write" v-if="userStore.isLoggedIn()">写文章</router-link>
        <router-link to="/admin" v-if="userStore.isAdmin()">管理后台</router-link>
      </nav>
      <div class="nav-actions">
        <el-input
          v-model="keyword"
          placeholder="搜索文章"
          class="search-input"
          @keyup.enter="doSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="doSearch" />
          </template>
        </el-input>

        <template v-if="userStore.isLoggedIn()">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="30" :src="userStore.user?.avatar">
                {{ userStore.user?.nickname?.[0] || 'U' }}
              </el-avatar>
              <span class="nickname">{{ userStore.user?.nickname }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="me">我的主页</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')

function doSearch() {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { q: keyword.value.trim() } })
  }
}

function handleCommand(command) {
  if (command === 'me') {
    router.push('/me')
  } else if (command === 'logout') {
    userStore.logout()
    router.push('/')
  }
}
</script>

<style scoped>
.nav-bar {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
  white-space: nowrap;
}

.nav-links {
  display: flex;
  gap: 20px;
  flex: 1;
}

.nav-links a {
  color: #606266;
  font-size: 15px;
}

.nav-links a.router-link-active {
  color: #409eff;
  font-weight: 600;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 200px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.nickname {
  font-size: 14px;
  color: #303133;
}
</style>

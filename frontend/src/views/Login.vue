<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-title">登录</h1>
      <el-form :model="form" @submit.prevent="handleLogin">
        <el-form-item>
          <el-input
            v-model="form.account"
            placeholder="用户名或邮箱"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>
      <p class="auth-switch">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ account: '', password: '' })
const loading = ref(false)

async function handleLogin() {
  if (!form.value.account || !form.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    await userStore.login(form.value.account, form.value.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.auth-card {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  width: 380px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.auth-title {
  text-align: center;
  margin-bottom: 24px;
  color: #303133;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
}

.auth-switch {
  text-align: center;
  margin-top: 16px;
  color: #909399;
  font-size: 14px;
}

.auth-switch a {
  color: #409eff;
}
</style>

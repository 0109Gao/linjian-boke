<template>
  <div class="auth-page">
    <div class="auth-card">
      <h1 class="auth-title">注册</h1>
      <el-form :model="form">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.email" placeholder="邮箱" :prefix-icon="Message" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.nickname" placeholder="昵称（可选）" :prefix-icon="Avatar" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码（至少 6 位）"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">
          注册
        </el-button>
      </el-form>
      <p class="auth-switch">
        已有账号？<router-link to="/login">立即登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message, Avatar } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const form = ref({ username: '', email: '', nickname: '', password: '' })
const loading = ref(false)

async function handleRegister() {
  if (!form.value.username || !form.value.email || !form.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (form.value.password.length < 6) {
    ElMessage.warning('密码至少 6 位')
    return
  }
  loading.value = true
  try {
    const data = await userStore.register(form.value)
    ElMessage.success(data.user.role === 'ADMIN' ? '注册成功，你已成为站长！' : '注册成功')
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

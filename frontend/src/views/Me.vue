<template>
  <div>
    <NavBar />
    <main class="container">
      <div class="profile-card">
        <el-avatar :size="80" :src="userStore.user?.avatar">
          {{ userStore.user?.nickname?.[0] || 'U' }}
        </el-avatar>
        <div class="profile-info">
          <h2>
            {{ userStore.user?.nickname }}
            <el-tag v-if="genderText" size="small" type="info" class="gender-tag">{{ genderText }}</el-tag>
          </h2>
          <p class="role">{{ userStore.isAdmin() ? '站长' : '会员' }}</p>
          <p class="bio">{{ userStore.user?.bio || '这个人很懒，什么都没写' }}</p>
        </div>
        <el-button type="primary" plain class="edit-btn" @click="openEdit">
          <el-icon><Edit /></el-icon> 编辑资料
        </el-button>
      </div>

      <h3 class="section-title">我的文章</h3>
      <el-empty v-if="myPosts.length === 0" description="还没有发布过文章" />
      <div v-for="post in myPosts" :key="post.id" class="my-post">
        <div class="my-post-main" @click="goDetail(post.id)">
          <span class="post-title">{{ post.title }}</span>
          <span class="post-time">{{ formatTime(post.createdAt) }}</span>
        </div>
        <div class="my-post-actions">
          <el-tag :type="statusType(post.status)" size="small">{{ statusText(post.status) }}</el-tag>
          <el-button text type="primary" size="small" @click="editPost(post.id)">编辑</el-button>
          <el-button text type="danger" size="small" @click="delPost(post.id)">删除</el-button>
        </div>
      </div>
    </main>

    <!-- 编辑资料弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑个人资料" width="480px" :close-on-click-modal="false">
      <el-form :model="form" label-width="70px">
        <el-form-item label="头像">
          <div class="avatar-uploader">
            <el-avatar :size="72" :src="form.avatar">
              {{ form.nickname?.[0] || 'U' }}
            </el-avatar>
            <el-upload
              :show-file-list="false"
              :http-request="handleUpload"
              :before-upload="beforeUpload"
              accept="image/*"
            >
              <el-button size="small" :loading="uploading">
                <el-icon><Upload /></el-icon> 上传头像
              </el-button>
            </el-upload>
          </div>
          <div class="upload-tip">支持 JPG / PNG / GIF / WebP，大小不超过 2MB</div>
        </el-form-item>

        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="50" />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="MALE">男</el-radio>
            <el-radio value="FEMALE">女</el-radio>
            <el-radio value="SECRET">保密</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="简介">
          <el-input
            v-model="form.bio"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="介绍一下自己吧"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Upload } from '@element-plus/icons-vue'
import NavBar from '../components/NavBar.vue'
import { getMyPosts, deletePost, updateProfile, uploadAvatar } from '../api'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const myPosts = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const uploading = ref(false)
const form = ref({ nickname: '', bio: '', gender: 'SECRET', avatar: '' })

const genderMap = { MALE: '男', FEMALE: '女', SECRET: '保密' }
const genderText = computed(() => genderMap[userStore.user?.gender] || '')

async function loadMyPosts() {
  myPosts.value = await getMyPosts()
}

function openEdit() {
  const u = userStore.user || {}
  form.value = {
    nickname: u.nickname || '',
    bio: u.bio || '',
    gender: u.gender || 'SECRET',
    avatar: u.avatar || ''
  }
  dialogVisible.value = true
}

function beforeUpload(file) {
  const okType = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!okType) {
    ElMessage.error('仅支持 JPG / PNG / GIF / WebP 格式')
    return false
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

async function handleUpload({ file }) {
  uploading.value = true
  try {
    const url = await uploadAvatar(file)
    form.value.avatar = url
    ElMessage.success('头像上传成功')
  } finally {
    uploading.value = false
  }
}

async function saveProfile() {
  if (!form.value.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  saving.value = true
  try {
    const updated = await updateProfile({
      nickname: form.value.nickname.trim(),
      bio: form.value.bio,
      gender: form.value.gender,
      avatar: form.value.avatar
    })
    userStore.setUser(updated)
    ElMessage.success('资料已更新')
    dialogVisible.value = false
  } finally {
    saving.value = false
  }
}

function goDetail(id) {
  router.push(`/post/${id}`)
}

function editPost(id) {
  router.push({ path: '/write', query: { edit: id } })
}

async function delPost(id) {
  await ElMessageBox.confirm('确定删除这篇文章吗？', '提示', { type: 'warning' })
  await deletePost(id)
  ElMessage.success('删除成功')
  loadMyPosts()
}

function statusType(status) {
  return status === 'PUBLISHED' ? 'success' : status === 'PENDING' ? 'warning' : 'info'
}

function statusText(status) {
  return status === 'PUBLISHED' ? '已发布' : status === 'PENDING' ? '待审核' : '草稿'
}

function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(loadMyPosts)
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.profile-card {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  display: flex;
  gap: 24px;
  align-items: center;
  margin-bottom: 20px;
  position: relative;
}

.profile-info {
  flex: 1;
}

.profile-info h2 {
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.gender-tag {
  vertical-align: middle;
}

.role {
  color: #409eff;
  font-size: 13px;
  margin-bottom: 8px;
}

.bio {
  color: #909399;
  font-size: 14px;
}

.edit-btn {
  align-self: flex-start;
}

.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 16px;
}

.upload-tip {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 6px;
}

.section-title {
  margin: 24px 0 12px;
}

.my-post {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.my-post-main {
  flex: 1;
  cursor: pointer;
}

.post-title {
  font-weight: 600;
  color: #303133;
}

.post-time {
  color: #c0c4cc;
  font-size: 13px;
  margin-left: 12px;
}

.my-post-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>

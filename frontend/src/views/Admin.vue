<template>
  <div>
    <NavBar />
    <main class="container">
      <h1 class="page-title">管理后台</h1>

      <el-tabs v-model="activeTab">
        <!-- 文章审核 -->
        <el-tab-pane label="文章审核" name="posts">
          <el-empty v-if="pendingPosts.length === 0" description="暂无待审核文章" />
          <div v-for="post in pendingPosts" :key="post.id" class="audit-item">
            <div class="audit-main">
              <h3>{{ post.title }}</h3>
              <p class="audit-meta">{{ post.authorName }} · {{ formatTime(post.createdAt) }}</p>
            </div>
            <div class="audit-actions">
              <el-button type="success" size="small" @click="auditPost(post.id, 'PUBLISHED')">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="auditPost(post.id, 'PENDING')">
                驳回
              </el-button>
            </div>
          </div>
        </el-tab-pane>

        <!-- 评论审核 -->
        <el-tab-pane label="评论审核" name="comments">
          <el-empty v-if="pendingComments.length === 0" description="暂无待审核评论" />
          <div v-for="comment in pendingComments" :key="comment.id" class="audit-item">
            <div class="audit-main">
              <p class="audit-content">{{ comment.content }}</p>
              <p class="audit-meta">评论 ID: {{ comment.id }}</p>
            </div>
            <div class="audit-actions">
              <el-button type="success" size="small" @click="auditComment(comment.id, 'APPROVED')">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="auditComment(comment.id, 'SPAM')">
                删除
              </el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'
import request from '../api/request'

const activeTab = ref('posts')
const pendingPosts = ref([])
const pendingComments = ref([])

async function loadPendingPosts() {
  pendingPosts.value = await request.get('/admin/posts/pending')
}

async function loadPendingComments() {
  pendingComments.value = await request.get('/admin/comments/pending')
}

async function auditPost(id, status) {
  await request.put(`/admin/posts/${id}/audit`, { status })
  ElMessage.success(status === 'PUBLISHED' ? '已通过发布' : '已驳回')
  loadPendingPosts()
}

async function auditComment(id, status) {
  await request.put(`/admin/comments/${id}/audit`, { status })
  ElMessage.success('操作成功')
  loadPendingComments()
}

function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  loadPendingPosts()
  loadPendingComments()
})
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: 20px auto;
  padding: 0 20px;
}

.page-title {
  margin-bottom: 20px;
}

.audit-item {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.audit-main {
  flex: 1;
}

.audit-main h3 {
  margin-bottom: 8px;
}

.audit-content {
  color: #606266;
}

.audit-meta {
  color: #c0c4cc;
  font-size: 13px;
}

.audit-actions {
  display: flex;
  gap: 8px;
}
</style>

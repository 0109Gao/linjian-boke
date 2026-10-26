<template>
  <div>
    <NavBar />
    <main class="container" v-if="post">
      <article class="post-detail">
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-meta">
          <span>{{ post.authorName || '匿名' }}</span>
          <span>{{ post.categoryName || '未分类' }}</span>
          <span>{{ formatTime(post.createdAt) }}</span>
          <span>👁 {{ post.views }}</span>
        </div>
        <el-image v-if="post.cover" :src="post.cover" class="post-cover" fit="cover" />
        <div class="post-content" v-html="renderedContent"></div>

        <!-- 互动 -->
        <div class="interactions">
          <el-button :type="liked ? 'danger' : 'default'" @click="handleLike">
            👍 点赞 {{ post.likeCount }}
          </el-button>
          <el-button :type="favorited ? 'warning' : 'default'" @click="handleFavorite">
            ⭐ {{ favorited ? '已收藏' : '收藏' }}
          </el-button>
        </div>
      </article>

      <!-- 评论区 -->
      <section class="comments">
        <h2 class="comments-title">评论（{{ commentCount }}）</h2>
        <div v-if="userStore.isLoggedIn()" class="comment-form">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
          />
          <el-button type="primary" class="comment-submit" @click="submitComment">
            发表评论
          </el-button>
        </div>
        <div v-else class="comment-login-tip">
          <router-link to="/login">登录</router-link> 后即可评论
        </div>

        <!-- 评论列表 -->
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <el-avatar :size="32" :src="comment.authorAvatar">
              {{ comment.authorName?.[0] || 'U' }}
            </el-avatar>
            <span class="comment-author">{{ comment.authorName }}</span>
            <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
          </div>
          <p class="comment-content">{{ comment.content }}</p>
          <el-button text type="primary" size="small" @click="replyTo(comment)">
            回复
          </el-button>

          <!-- 子评论（盖楼） -->
          <div v-if="comment.children?.length" class="child-comments">
            <div v-for="child in comment.children" :key="child.id" class="child-comment">
              <span class="child-author">{{ child.authorName }}</span>
              <span class="child-content">{{ child.content }}</span>
              <span class="comment-time">{{ formatTime(child.createdAt) }}</span>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import NavBar from '../components/NavBar.vue'
import { getPostDetail, getComments, createComment, toggleLike, toggleFavorite } from '../api'
import { useUserStore } from '../store/user'

const route = useRoute()
const userStore = useUserStore()
const post = ref(null)
const comments = ref([])
const newComment = ref('')
const liked = ref(false)
const favorited = ref(false)
const replyParentId = ref(null)

const renderedContent = computed(() => {
  return post.value?.content ? marked.parse(post.value.content) : ''
})

const commentCount = computed(() => {
  let count = comments.value.length
  comments.value.forEach(c => { count += c.children?.length || 0 })
  return count
})

async function loadPost() {
  post.value = await getPostDetail(route.params.id)
}

async function loadComments() {
  comments.value = await getComments(route.params.id)
}

async function handleLike() {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    return
  }
  const result = await toggleLike({ targetType: 'POST', targetId: post.value.id })
  liked.value = result.liked
  post.value.likeCount += result.liked ? 1 : -1
}

async function handleFavorite() {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    return
  }
  const result = await toggleFavorite(post.value.id)
  favorited.value = result.favorited
  ElMessage.success(result.favorited ? '收藏成功' : '已取消收藏')
}

async function submitComment() {
  if (!newComment.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }
  await createComment(post.value.id, {
    content: newComment.value.trim(),
    parentId: replyParentId.value
  })
  newComment.value = ''
  replyParentId.value = null
  ElMessage.success('评论成功')
  loadComments()
}

function replyTo(comment) {
  replyParentId.value = comment.id
  newComment.value = `回复 @${comment.authorName}：`
}

function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  loadPost()
  loadComments()
})
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.post-detail {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
}

.post-title {
  font-size: 28px;
  margin-bottom: 16px;
}

.post-meta {
  color: #909399;
  font-size: 14px;
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.post-cover {
  width: 100%;
  max-height: 400px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.post-content {
  line-height: 1.8;
  font-size: 16px;
  color: #303133;
}

.post-content :deep(img) {
  max-width: 100%;
}

.interactions {
  margin-top: 32px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 12px;
}

.comments {
  background: #fff;
  border-radius: 8px;
  padding: 24px 32px;
  margin-top: 20px;
}

.comments-title {
  font-size: 18px;
  margin-bottom: 16px;
}

.comment-form {
  margin-bottom: 24px;
}

.comment-submit {
  margin-top: 12px;
}

.comment-login-tip {
  color: #909399;
  padding: 16px 0;
  margin-bottom: 16px;
}

.comment-login-tip a {
  color: #409eff;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f5f7fa;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.comment-author {
  font-weight: 600;
  color: #303133;
}

.comment-time {
  color: #c0c4cc;
  font-size: 12px;
}

.comment-content {
  margin: 8px 0 8px 42px;
  color: #606266;
  line-height: 1.6;
}

.child-comments {
  margin: 12px 0 0 42px;
  padding: 12px;
  background: #f8f9fb;
  border-radius: 6px;
}

.child-comment {
  padding: 6px 0;
  font-size: 14px;
}

.child-author {
  color: #409eff;
  margin-right: 8px;
}

.child-content {
  color: #606266;
}

.child-comment .comment-time {
  margin-left: 12px;
}
</style>

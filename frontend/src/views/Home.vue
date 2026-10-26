<template>
  <div>
    <NavBar />
    <main class="container">
      <!-- 分类筛选 -->
      <div class="category-bar">
        <el-tag
          :type="activeCategory === null ? 'primary' : 'info'"
          class="category-tag"
          @click="selectCategory(null)"
        >
          全部
        </el-tag>
        <el-tag
          v-for="cat in categories"
          :key="cat.id"
          :type="activeCategory === cat.id ? 'primary' : 'info'"
          class="category-tag"
          @click="selectCategory(cat.id)"
        >
          {{ cat.name }}
        </el-tag>
      </div>

      <!-- 文章列表 -->
      <el-skeleton :loading="loading" animated :count="3">
        <template #default>
          <div v-if="posts.length === 0" class="empty">
            <el-empty description="暂无文章" />
          </div>
          <div v-for="post in posts" :key="post.id" class="post-card" @click="goDetail(post.id)">
            <div class="post-main">
              <h2 class="post-title">
                <span v-if="post.pinned" class="pinned-tag">置顶</span>
                {{ post.title }}
              </h2>
              <p class="post-summary">{{ post.summary || '（无摘要）' }}</p>
              <div class="post-meta">
                <span>{{ post.authorName || '匿名' }}</span>
                <span>{{ post.categoryName || '未分类' }}</span>
                <span>{{ formatTime(post.createdAt) }}</span>
                <span>👁 {{ post.views }}</span>
                <span>👍 {{ post.likeCount }}</span>
              </div>
            </div>
            <el-image
              v-if="post.cover"
              :src="post.cover"
              class="post-cover"
              fit="cover"
            />
          </div>
        </template>
      </el-skeleton>

      <!-- 分页 -->
      <el-pagination
        v-if="total > size"
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev, pager, next"
        class="pagination"
        @current-change="loadPosts"
      />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import { getPosts, getCategories } from '../api'

const router = useRouter()
const posts = ref([])
const categories = ref([])
const activeCategory = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(true)

async function loadPosts() {
  loading.value = true
  const params = { page: page.value, size: size.value }
  if (activeCategory.value) {
    params.categoryId = activeCategory.value
  }
  const data = await getPosts(params)
  posts.value = data.records || []
  total.value = data.total || 0
  loading.value = false
}

async function loadCategories() {
  categories.value = await getCategories()
}

function selectCategory(id) {
  activeCategory.value = id
  page.value = 1
  loadPosts()
}

function goDetail(id) {
  router.push(`/post/${id}`)
}

function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  loadPosts()
  loadCategories()
})
</script>

<style scoped>
.container {
  max-width: 1100px;
  margin: 20px auto;
  padding: 0 20px;
}

.category-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.category-tag {
  cursor: pointer;
}

.post-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  display: flex;
  gap: 20px;
  transition: box-shadow 0.2s;
}

.post-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.post-main {
  flex: 1;
}

.post-title {
  font-size: 20px;
  margin-bottom: 10px;
  color: #303133;
}

.pinned-tag {
  display: inline-block;
  background: #f56c6c;
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  margin-right: 8px;
}

.post-summary {
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  color: #c0c4cc;
  font-size: 13px;
  display: flex;
  gap: 16px;
}

.post-cover {
  width: 160px;
  height: 100px;
  border-radius: 6px;
  flex-shrink: 0;
}

.empty {
  padding: 40px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>

<template>
  <div>
    <NavBar />
    <main class="container">
      <h1 class="page-title">搜索：{{ keyword }}</h1>
      <el-empty v-if="results.length === 0" description="没有找到相关文章" />
      <div v-for="post in results" :key="post.id" class="result-item" @click="goDetail(post.id)">
        <h3 class="result-title">{{ post.title }}</h3>
        <p class="result-summary">{{ post.summary || '（无摘要）' }}</p>
        <div class="result-meta">
          <span>{{ post.authorName || '匿名' }}</span>
          <span>{{ formatTime(post.createdAt) }}</span>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import { searchPosts } from '../api'

const route = useRoute()
const router = useRouter()
const keyword = ref(route.query.q || '')
const results = ref([])

async function doSearch() {
  if (keyword.value) {
    results.value = await searchPosts(keyword.value)
  }
}

function goDetail(id) {
  router.push(`/post/${id}`)
}

function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

watch(() => route.query.q, (val) => {
  keyword.value = val || ''
  doSearch()
})

onMounted(doSearch)
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.page-title {
  margin-bottom: 20px;
}

.result-item {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 12px;
  cursor: pointer;
}

.result-title {
  margin-bottom: 8px;
  color: #303133;
}

.result-summary {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.result-meta {
  color: #c0c4cc;
  font-size: 13px;
  display: flex;
  gap: 16px;
}
</style>

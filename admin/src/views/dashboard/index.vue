<template>
  <div>
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" :style="{ color: card.color }">
              <component :is="card.icon" />
            </el-icon>
            <div>
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待审核提醒 -->
    <el-card shadow="hover" style="margin-top: 20px" v-if="pendingPostCount > 0 || pendingCommentCount > 0">
      <template #header>
        <span><el-icon style="vertical-align: middle"><Bell /></el-icon> 待处理事项</span>
      </template>
      <div class="pending-list">
        <el-alert
          v-if="pendingPostCount > 0"
          :title="`有 ${pendingPostCount} 篇文章待审核`"
          type="warning"
          show-icon
          :closable="false"
          style="margin-bottom: 10px"
        >
          <el-button type="warning" size="small" @click="$router.push('/posts')">去处理</el-button>
        </el-alert>
        <el-alert
          v-if="pendingCommentCount > 0"
          :title="`有 ${pendingCommentCount} 条评论待审核`"
          type="warning"
          show-icon
          :closable="false"
        >
          <el-button type="warning" size="small" @click="$router.push('/comments')">去处理</el-button>
        </el-alert>
      </div>
    </el-card>

    <!-- 最近内容 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>最近文章</span></template>
          <el-table :data="recentPosts" size="small">
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>最近评论</span></template>
          <el-table :data="recentComments" size="small">
            <el-table-column prop="authorName" label="用户" width="100" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '../../api'

const data = ref({})
const recentPosts = ref([])
const recentComments = ref([])

const pendingPostCount = computed(() => data.value.pendingPostCount || 0)
const pendingCommentCount = computed(() => data.value.pendingCommentCount || 0)

const statCards = computed(() => [
  { label: '文章总数', value: data.value.postCount ?? 0, icon: 'Document', color: '#409EFF' },
  { label: '用户总数', value: data.value.userCount ?? 0, icon: 'User', color: '#67C23A' },
  { label: '评论总数', value: data.value.commentCount ?? 0, icon: 'ChatDotRound', color: '#E6A23C' },
  { label: '待审核文章', value: data.value.pendingPostCount ?? 0, icon: 'Warning', color: '#F56C6C' }
])

function statusType(status) {
  return { PUBLISHED: 'success', PENDING: 'warning', DRAFT: 'info' }[status] || 'info'
}
function statusText(status) {
  return { PUBLISHED: '已发布', PENDING: '待审核', DRAFT: '草稿' }[status] || status
}

onMounted(async () => {
  const res = await getDashboard()
  data.value = res.data || {}
  recentPosts.value = data.value.recentPosts || []
  recentComments.value = data.value.recentComments || []
})
</script>

<style scoped>
.stat-card {
  cursor: pointer;
}
.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  font-size: 44px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}
.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}
</style>

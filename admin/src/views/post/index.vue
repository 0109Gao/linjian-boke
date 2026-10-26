<template>
  <div>
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <div class="toolbar">
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="草稿" value="DRAFT" />
        </el-select>
        <el-input v-model="query.keyword" placeholder="搜索标题/摘要" clearable style="width: 240px" @keyup.enter="loadData" @clear="loadData">
          <template #append>
            <el-button @click="loadData"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
      </div>

      <el-table :data="list" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="views" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column label="置顶" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.pinned === 1" type="danger" size="small">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="success" size="small" @click="handleAudit(row, 'PUBLISHED')">通过</el-button>
            <el-button v-if="row.status === 'PENDING'" type="warning" size="small" @click="handleAudit(row, 'DRAFT')">驳回</el-button>
            <el-button size="small" @click="handlePin(row)">{{ row.pinned === 1 ? '取消置顶' : '置顶' }}</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPosts, auditPost, togglePin, deletePost } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, status: '', keyword: '' })

function statusType(status) {
  return { PUBLISHED: 'success', PENDING: 'warning', DRAFT: 'info' }[status] || 'info'
}
function statusText(status) {
  return { PUBLISHED: '已发布', PENDING: '待审核', DRAFT: '草稿' }[status] || status
}

async function loadData() {
  loading.value = true
  try {
    const res = await getPosts(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function handleAudit(row, status) {
  const action = status === 'PUBLISHED' ? '通过' : '驳回'
  await ElMessageBox.confirm(`确定${action}文章「${row.title}」吗？`, '提示', { type: 'warning' })
  await auditPost(row.id, status)
  ElMessage.success(`${action}成功`)
  loadData()
}

async function handlePin(row) {
  await togglePin(row.id, row.pinned === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除文章「${row.title}」吗？此操作不可恢复`, '警告', { type: 'error' })
  await deletePost(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

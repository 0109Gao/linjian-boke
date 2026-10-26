<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px" @change="loadData">
          <el-option label="已通过" value="APPROVED" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="垃圾" value="SPAM" />
        </el-select>
      </div>

      <el-table :data="list" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="authorName" label="用户" width="120" />
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
        <el-table-column prop="postId" label="文章ID" width="90" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="success" size="small" @click="handleAudit(row, 'APPROVED')">通过</el-button>
            <el-button v-if="row.status !== 'SPAM'" type="warning" size="small" @click="handleAudit(row, 'SPAM')">垃圾</el-button>
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
import { getComments, auditComment, deleteComment } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, status: '' })

function statusType(status) {
  return { APPROVED: 'success', PENDING: 'warning', SPAM: 'danger' }[status] || 'info'
}
function statusText(status) {
  return { APPROVED: '已通过', PENDING: '待审核', SPAM: '垃圾' }[status] || status
}
function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 19) : '-'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getComments(query)
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

async function handleAudit(row, status) {
  await auditComment(row.id, status)
  ElMessage.success('操作成功')
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该评论吗？', '警告', { type: 'error' })
  await deleteComment(row.id)
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

<template>
  <div>
    <el-row :gutter="20">
      <!-- 分类管理 -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>分类管理</span>
              <el-button type="primary" size="small" @click="openCategoryDialog()">新增分类</el-button>
            </div>
          </template>
          <el-table :data="categories" v-loading="loadingCat">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="sort" label="排序" width="80" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button size="small" @click="openCategoryDialog(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeleteCategory(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 标签管理 -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>标签管理</span>
              <el-button type="primary" size="small" @click="openTagDialog()">新增标签</el-button>
            </div>
          </template>
          <el-table :data="tags" v-loading="loadingTag">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="名称" />
            <el-table-column label="操作" width="140">
              <template #default="{ row }">
                <el-button size="small" @click="openTagDialog(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeleteTag(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分类编辑弹窗 -->
    <el-dialog v-model="categoryDialog" :title="categoryForm.id ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="categoryForm.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveCategory">保存</el-button>
      </template>
    </el-dialog>

    <!-- 标签编辑弹窗 -->
    <el-dialog v-model="tagDialog" :title="tagForm.id ? '编辑标签' : '新增标签'" width="400px">
      <el-form :model="tagForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="tagForm.name" placeholder="标签名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tagDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTag">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategories, saveCategory, deleteCategory, getTags, saveTag, deleteTag } from '../../api'

const categories = ref([])
const tags = ref([])
const loadingCat = ref(false)
const loadingTag = ref(false)

const categoryDialog = ref(false)
const categoryForm = reactive({ id: null, name: '', sort: 0 })
const tagDialog = ref(false)
const tagForm = reactive({ id: null, name: '' })

async function loadCategories() {
  loadingCat.value = true
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } finally {
    loadingCat.value = false
  }
}

async function loadTags() {
  loadingTag.value = true
  try {
    const res = await getTags()
    tags.value = res.data || []
  } finally {
    loadingTag.value = false
  }
}

function openCategoryDialog(row) {
  if (row) {
    Object.assign(categoryForm, { id: row.id, name: row.name, sort: row.sort })
  } else {
    Object.assign(categoryForm, { id: null, name: '', sort: 0 })
  }
  categoryDialog.value = true
}

async function handleSaveCategory() {
  if (!categoryForm.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  await saveCategory({ ...categoryForm })
  ElMessage.success('保存成功')
  categoryDialog.value = false
  loadCategories()
}

async function handleDeleteCategory(row) {
  await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '警告', { type: 'error' })
  try {
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (e) {
    // 分类下有文章等错误已由拦截器提示
  }
}

function openTagDialog(row) {
  if (row) {
    Object.assign(tagForm, { id: row.id, name: row.name })
  } else {
    Object.assign(tagForm, { id: null, name: '' })
  }
  tagDialog.value = true
}

async function handleSaveTag() {
  if (!tagForm.name) {
    ElMessage.warning('请输入标签名称')
    return
  }
  await saveTag({ ...tagForm })
  ElMessage.success('保存成功')
  tagDialog.value = false
  loadTags()
}

async function handleDeleteTag(row) {
  await ElMessageBox.confirm(`确定删除标签「${row.name}」吗？`, '警告', { type: 'error' })
  await deleteTag(row.id)
  ElMessage.success('删除成功')
  loadTags()
}

onMounted(() => {
  loadCategories()
  loadTags()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>

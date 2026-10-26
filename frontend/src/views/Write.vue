<template>
  <div>
    <NavBar />
    <main class="container">
      <h1 class="page-title">{{ isEdit ? '编辑文章' : '写文章' }}</h1>
      <p class="page-tip" v-if="!isEdit && !userStore.isAdmin()">
        提交后需站长审核，通过后才会公开显示。
      </p>

      <el-form :model="form" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="200" />
        </el-form-item>

        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" clearable>
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="标签">
          <el-select v-model="form.tagIds" multiple placeholder="选择标签">
            <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" maxlength="500"
            placeholder="简要描述文章内容（可选）" />
        </el-form-item>

        <el-form-item label="封面图 URL">
          <el-input v-model="form.cover" placeholder="粘贴图片链接（可选）" />
        </el-form-item>

        <el-form-item label="正文（支持 Markdown）">
          <el-input v-model="form.content" type="textarea" :rows="16"
            placeholder="# 标题&#10;&#10;正文内容..." />
        </el-form-item>

        <el-button type="primary" :loading="submitting" @click="submit">
          {{ isEdit ? '保存修改' : '提交审核' }}
        </el-button>
      </el-form>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import NavBar from '../components/NavBar.vue'
import { getCategories, getTags, createPost, updatePost, getPostDetail } from '../api'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const categories = ref([])
const tags = ref([])
const submitting = ref(false)
const form = ref({ title: '', categoryId: null, tagIds: [], summary: '', cover: '', content: '' })

const isEdit = computed(() => !!route.query.edit)

async function loadOptions() {
  categories.value = await getCategories()
  tags.value = await getTags()
}

async function loadEditPost() {
  if (route.query.edit) {
    const post = await getPostDetail(route.query.edit)
    form.value.title = post.title
    form.value.categoryId = post.categoryId
    form.value.summary = post.summary
    form.value.cover = post.cover
    form.value.content = post.content
    form.value.tagIds = []
  }
}

async function submit() {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!form.value.content.trim()) {
    ElMessage.warning('请输入正文')
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updatePost(route.query.edit, form.value)
      ElMessage.success('修改成功，等待重新审核')
    } else {
      await createPost(form.value)
      ElMessage.success(userStore.isAdmin() ? '发布成功' : '提交成功，等待站长审核')
    }
    router.push('/')
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadOptions()
  loadEditPost()
})
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 8px;
}

.page-tip {
  color: #e6a23c;
  font-size: 14px;
  margin-bottom: 20px;
}
</style>

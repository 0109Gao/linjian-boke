<template>
  <div>
    <el-card shadow="never">
      <template #header><span>站点设置</span></template>
      <el-form label-width="140px" style="max-width: 600px">
        <el-form-item v-for="item in configs" :key="item.id" :label="configLabel(item.configKey)">
          <template v-if="item.configKey === 'comment_moderation'">
            <el-switch
              v-model="item.configValue"
              active-value="true"
              inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="handleSave(item)"
            />
            <div class="tip">开启后，会员发布的评论需站长审核后才会显示</div>
          </template>
          <template v-else>
            <el-input v-model="item.configValue" @blur="handleSave(item)" />
          </template>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfigs, saveConfig } from '../../api'

const configs = ref([])

function configLabel(key) {
  return {
    site_name: '站点名称',
    comment_moderation: '评论审核',
    domain: '站点域名'
  }[key] || key
}

async function loadConfigs() {
  const res = await getConfigs()
  configs.value = res.data || []
}

async function handleSave(item) {
  await saveConfig({ id: item.id, configKey: item.configKey, configValue: item.configValue })
  ElMessage.success('保存成功')
}

onMounted(loadConfigs)
</script>

<style scoped>
.tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>

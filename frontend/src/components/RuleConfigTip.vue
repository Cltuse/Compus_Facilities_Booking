<template>
  <div class="rule-config-tip">
    <div class="tip-header">
      <el-icon><InfoFilled /></el-icon>
      <span>预约规则</span>
      <el-button 
        v-if="showDetailBtn" 
        type="text" 
        size="small" 
        @click="showDetail = !showDetail"
      >
        {{ showDetail ? '收起' : '查看详情' }}
      </el-button>
    </div>
    
    <div v-if="showDetail" class="tip-content">
      <div v-if="loading" class="loading-text">
        <el-icon class="is-loading"><Loading /></el-icon>
        加载规则中...
      </div>
      
      <div v-else-if="ruleDescription" class="rule-description">
        {{ ruleDescription }}
      </div>
      
      <div v-else class="no-rule">
        暂无特殊预约规则限制
      </div>
    </div>
    
    <div v-else class="tip-summary">
      <span v-if="ruleDescription">{{ getSummaryText(ruleDescription) }}</span>
      <span v-else>无特殊规则限制</span>
    </div>
  </div>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userClientAPI } from '@/api/userClient'

export default {
  name: 'RuleConfigTip',
  props: {
    categoryId: {
      type: Number,
      required: true
    },
    showDetailBtn: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const loading = ref(false)
    const showDetail = ref(false)
    const ruleDescription = ref('')

    const loadRuleDescription = async () => {
      if (!props.categoryId) return
      
      loading.value = true
      try {
        const response = await userClientAPI.getRuleDescription(props.categoryId)
        if (response.code === 200) {
          ruleDescription.value = response.data
        } else {
          ruleDescription.value = ''
        }
      } catch (error) {
        console.error('获取规则描述失败:', error)
        ruleDescription.value = ''
      } finally {
        loading.value = false
      }
    }

    const getSummaryText = (description) => {
      if (!description) return ''
      // 取前50个字符作为摘要
      return description.length > 50 ? description.substring(0, 50) + '...' : description
    }

    // 监听categoryId变化
    watch(() => props.categoryId, (newVal) => {
      if (newVal) {
        loadRuleDescription()
      }
    })

    onMounted(() => {
      if (props.categoryId) {
        loadRuleDescription()
      }
    })

    return {
      loading,
      showDetail,
      ruleDescription,
      getSummaryText
    }
  }
}
</script>

<style scoped>
.rule-config-tip {
  border: 1px solid #e1f5fe;
  border-radius: 8px;
  background: #f1f8ff;
  padding: 15px;
  margin: 15px 0;
}

.tip-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1976d2;
  margin-bottom: 10px;
}

.tip-header .el-icon {
  color: #1976d2;
}

.tip-content {
  border-top: 1px solid #e1f5fe;
  padding-top: 10px;
}

.loading-text {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 0.9rem;
}

.rule-description {
  color: #2c3e50;
  line-height: 1.6;
  font-size: 0.9rem;
}

.no-rule {
  color: #7f8c8d;
  font-style: italic;
  font-size: 0.9rem;
}

.tip-summary {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
}
</style>
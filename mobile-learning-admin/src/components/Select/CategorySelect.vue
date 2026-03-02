<template>
  <a-select
    v-model:value="selectedValue"
    :placeholder="placeholder"
    :loading="loading"
    :disabled="disabled"
    @change="handleChange"
  >
    <a-select-opt-group v-for="category in categories" :key="category.id" :label="category.categoryName">
      <a-select-option v-for="child in category.children" :key="child.id" :value="child.id">
        {{ child.categoryName }}
      </a-select-option>
    </a-select-opt-group>
    <a-select-option v-for="category in categories.filter(c => c.parentId === 0)" :key="category.id" :value="category.id">
      {{ category.categoryName }}
    </a-select-option>
  </a-select>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { categoryApi } from '@/api/category'

const props = defineProps({
  modelValue: [Number, String],
  placeholder: {
    type: String,
    default: '请选择分类'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const selectedValue = ref(props.modelValue)
const loading = ref(false)
const categories = ref([])

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await categoryApi.list()
    categories.value = res.data || []
  } catch (e) {
    message.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

const handleChange = (value) => {
  emit('update:modelValue', value)
  emit('change', value)
}

watch(() => props.modelValue, (val) => {
  selectedValue.value = val
})

onMounted(() => {
  loadCategories()
})
</script>

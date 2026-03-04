<template>
  <a-select
    v-model:value="selectedValue"
    :placeholder="placeholder"
    :loading="loading"
    :disabled="disabled"
    @change="handleChange"
  >
    <a-select-option
      v-for="teacher in teachers"
      :key="teacher.id"
      :value="teacher.id"
    >
      {{ teacher.teacherNo }} - {{ teacher.realName || teacher.nickname }}
    </a-select-option>
  </a-select>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { teacherApi } from '@/api/teacher'

const props = defineProps({
  modelValue: {
    type: [Number, String],
    default: undefined
  },
  placeholder: {
    type: String,
    default: '请选择教师'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const selectedValue = ref(props.modelValue)
const loading = ref(false)
const teachers = ref([])

const loadTeachers = async () => {
  loading.value = true
  try {
    const res = await teacherApi.list({ pageNum: 1, pageSize: 100 })
    teachers.value = res.data.records || []
  } catch (e) {
    message.error('加载教师列表失败')
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
  loadTeachers()
})
</script>

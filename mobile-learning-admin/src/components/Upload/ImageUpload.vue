<template>
  <a-upload
    name="file"
    :show-upload-list="false"
    :custom-request="handleUpload"
    :before-upload="beforeUpload"
  >
    <div
      v-if="imageUrl"
      class="image-preview"
    >
      <img
        :src="imageUrl"
        alt="preview"
      >
      <div class="image-mask">
        <span>更换图片</span>
      </div>
    </div>
    <div
      v-else
      class="upload-placeholder"
    >
      <PlusOutlined />
      <div style="margin-top: 8px">
        点击上传
      </div>
    </div>
  </a-upload>
</template>

<script setup>
import { ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import axios from 'axios'

const props = defineProps({
  modelValue: {
    type: String,
    default: undefined
  },
  accept: {
    type: String,
    default: 'image/*'
  },
  maxSize: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['update:modelValue'])

const imageUrl = ref(props.modelValue || '')

watch(() => props.modelValue, (val) => {
  imageUrl.value = val || ''
})

const beforeUpload = (file) => {
  const maxSize = props.maxSize * 1024 * 1024
  if (file.size > maxSize) {
    message.error(`文件大小不能超过${props.maxSize}MB`)
    return false
  }
  return true
}

const handleUpload = async ({ file, onSuccess, onError }) => {
  const formData = new FormData()
  formData.append('file', file)

  try {
    const token = localStorage.getItem('token')
    const { data } = await axios.post('/admin/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${token}`
      }
    })

    if (data.code === 200) {
      imageUrl.value = data.data.url
      emit('update:modelValue', data.data.url)
      message.success('上传成功')
      onSuccess()
    } else {
      message.error(data.message)
      onError()
    }
  } catch (e) {
    message.error('上传失败')
    onError(e)
  }
}
</script>

<style scoped>
.image-preview {
  width: 200px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-preview:hover .image-mask {
  opacity: 1;
}

.upload-placeholder {
  width: 200px;
  height: 120px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
}

.upload-placeholder:hover {
  border-color: #1890ff;
  color: #1890ff;
}
</style>
